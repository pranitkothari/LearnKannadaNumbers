package com.learnkannadanumbers.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.learnkannadanumbers.app.clearBreadcrumb
import com.learnkannadanumbers.app.data.KannadaNumbers
import com.learnkannadanumbers.app.data.KannadaWords
import com.learnkannadanumbers.app.data.PracticeCatalog
import com.learnkannadanumbers.app.data.PracticeItem
import com.learnkannadanumbers.app.data.ProgressRepository
import com.learnkannadanumbers.app.lastCrashFile
import com.learnkannadanumbers.app.readLastBreadcrumb
import com.learnkannadanumbers.app.speech.AudioRecorder
import com.learnkannadanumbers.app.speech.FuzzyMatch
import com.learnkannadanumbers.app.speech.KannadaTts
import com.learnkannadanumbers.app.speech.SpeechRecognizerManager
import com.learnkannadanumbers.app.writeBreadcrumb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface RoundState {
    data object Idle : RoundState
    data object Listening : RoundState
    data object Processing : RoundState
    data class Correct(val heard: String) : RoundState
    data class Incorrect(val heard: String, val expected: String, val expectedTransliteration: String) : RoundState
}

data class MainUiState(
    val screen: Screen = Screen.Home,
    val modelsReady: Boolean = false,
    val modelLoadError: String? = null,
    val lastCrash: String? = null,

    // Numbers practice (typed input, 0-100)
    val numberInput: String = "",
    val inputError: String? = null,

    // Currently selected item for Alphabet/Words/Review (tap-to-select or
    // auto-advanced) - Numbers builds its own item from numberInput instead.
    val currentItem: PracticeItem? = null,

    val roundState: RoundState = RoundState.Idle,

    // Review mode's queue of weak items, worked through one at a time.
    val reviewQueue: List<PracticeItem> = emptyList(),
    val reviewIndex: Int = 0,
    val reviewStarted: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private val audioRecorder = AudioRecorder()
    private val progressRepository = ProgressRepository(application)
    private var speechRecognizer: SpeechRecognizerManager? = null
    private var tts: KannadaTts? = null

    init {
        val app = getApplication<Application>()
        val crashFile = lastCrashFile(app)
        val breadcrumb = readLastBreadcrumb(app)
        val diagnostic = when {
            crashFile.exists() -> "Caught a Kotlin/Java exception:\n\n" + crashFile.readText()
            // A leftover breadcrumb that isn't the "finished" marker means the process
            // died (almost certainly a native/JNI crash) partway through a previous
            // launch, before ever reaching a point Java exception handling could see.
            breadcrumb != null && breadcrumb != "all models loaded" ->
                "No Kotlin exception was caught, which usually means a native (C/JNI) " +
                    "crash - those kill the process before Java can see anything.\n\n" +
                    "Last thing that started before the app died:\n$breadcrumb"
            else -> null
        }
        if (diagnostic != null) {
            _uiState.update { it.copy(lastCrash = diagnostic) }
        }

        viewModelScope.launch {
            try {
                writeBreadcrumb(app, "constructing KannadaTts (loads espeak_bridge native lib)")
                val kannadaTts = KannadaTts(app)
                withContext(Dispatchers.IO) {
                    writeBreadcrumb(app, "constructing SpeechRecognizerManager (sherpa-onnx + onnxruntime + Whisper model load)")
                    speechRecognizer = SpeechRecognizerManager(app)
                    writeBreadcrumb(app, "calling KannadaTts.init() (espeak_Initialize native call)")
                    kannadaTts.init()
                }
                tts = kannadaTts
                writeBreadcrumb(app, "all models loaded")
                _uiState.update { it.copy(modelsReady = true) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(modelLoadError = t.message ?: "Failed to load offline speech models") }
            }
        }
    }

    fun dismissLastCrash() {
        val app = getApplication<Application>()
        lastCrashFile(app).delete()
        clearBreadcrumb(app)
        _uiState.update { it.copy(lastCrash = null) }
    }

    // --- Navigation ---

    fun navigateTo(screen: Screen) {
        _uiState.update {
            it.copy(
                screen = screen,
                currentItem = null,
                roundState = RoundState.Idle,
                reviewQueue = emptyList(),
                reviewIndex = 0,
                reviewStarted = false,
            )
        }
    }

    fun navigateHome() = navigateTo(Screen.Home)

    // --- Numbers practice (typed input) ---

    fun onNumberInputChanged(text: String) {
        val digitsOnly = text.filter { it.isDigit() }
        val parsed = digitsOnly.toIntOrNull()
        val error = when {
            digitsOnly.isEmpty() -> null
            parsed == null || parsed !in 0..100 -> "Enter a number from 0 to 100"
            else -> null
        }
        _uiState.update {
            it.copy(numberInput = digitsOnly, inputError = error, roundState = RoundState.Idle)
        }
    }

    // --- Alphabet / Words tap-to-select ---

    fun selectItem(item: PracticeItem?) {
        _uiState.update { it.copy(currentItem = item, roundState = RoundState.Idle) }
    }

    fun openWordsCategory(category: KannadaWords.Category) {
        navigateTo(Screen.WordsPractice(category))
    }

    // --- Review mode ---

    fun startReview() {
        navigateTo(Screen.Review)
        viewModelScope.launch {
            val weakIds = progressRepository.weakestItemIds(limit = 20)
            val queue = weakIds.mapNotNull { PracticeCatalog.findById(it) }
            _uiState.update {
                it.copy(
                    reviewQueue = queue,
                    reviewIndex = 0,
                    reviewStarted = true,
                    currentItem = queue.firstOrNull(),
                    roundState = RoundState.Idle,
                )
            }
        }
    }

    fun reviewNext() {
        val state = _uiState.value
        val nextIndex = state.reviewIndex + 1
        if (nextIndex >= state.reviewQueue.size) {
            navigateHome()
            return
        }
        _uiState.update {
            it.copy(
                reviewIndex = nextIndex,
                currentItem = state.reviewQueue[nextIndex],
                roundState = RoundState.Idle,
            )
        }
    }

    // --- The practice round itself, shared by every mode ---

    private fun activeItem(state: MainUiState): PracticeItem? {
        if (state.screen == Screen.NumbersPractice) {
            val n = state.numberInput.toIntOrNull() ?: return null
            if (n !in 0..100) return null
            return PracticeItem(
                id = "number:$n",
                kannada = KannadaNumbers.wordFor(n),
                transliteration = KannadaNumbers.transliterationFor(n),
                displayLabel = n.toString(),
            )
        }
        return state.currentItem
    }

    fun canListen(state: MainUiState): Boolean =
        state.modelsReady &&
            activeItem(state) != null &&
            state.roundState !is RoundState.Listening &&
            state.roundState !is RoundState.Processing

    fun onMicTapped() {
        val state = _uiState.value
        val item = activeItem(state) ?: return
        val recognizer = speechRecognizer ?: return
        if (!canListen(state)) return

        _uiState.update { it.copy(roundState = RoundState.Listening) }

        viewModelScope.launch {
            val samples = audioRecorder.record()
            _uiState.update { it.copy(roundState = RoundState.Processing) }

            val heard = withContext(Dispatchers.IO) { recognizer.transcribe(samples) }
            val correct = FuzzyMatch.isMatch(heard, item.kannada)

            progressRepository.recordAttempt(item.id, correct)

            if (correct) {
                _uiState.update { it.copy(roundState = RoundState.Correct(heard)) }
            } else {
                _uiState.update {
                    it.copy(roundState = RoundState.Incorrect(heard, item.kannada, item.transliteration))
                }
                tts?.speak(item.kannada)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.release()
        tts?.release()
    }
}
