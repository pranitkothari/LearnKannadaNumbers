package com.learnkannadanumbers.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.learnkannadanumbers.app.clearBreadcrumb
import com.learnkannadanumbers.app.data.KannadaNumbers
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
    data class Incorrect(val heard: String, val expected: String) : RoundState
}

data class MainUiState(
    val numberInput: String = "",
    val inputError: String? = null,
    val modelsReady: Boolean = false,
    val modelLoadError: String? = null,
    val roundState: RoundState = RoundState.Idle,
    val lastCrash: String? = null,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private val audioRecorder = AudioRecorder()
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

    fun onMicTapped() {
        val state = _uiState.value
        val target = state.numberInput.toIntOrNull()
        if (target == null || target !in 0..100 || !state.modelsReady) return

        val recognizer = speechRecognizer ?: return
        _uiState.update { it.copy(roundState = RoundState.Listening) }

        viewModelScope.launch {
            val samples = audioRecorder.record()
            _uiState.update { it.copy(roundState = RoundState.Processing) }

            val heard = withContext(Dispatchers.IO) { recognizer.transcribe(samples) }
            val expected = KannadaNumbers.wordFor(target)

            if (FuzzyMatch.isMatch(heard, expected)) {
                _uiState.update { it.copy(roundState = RoundState.Correct(heard)) }
            } else {
                _uiState.update { it.copy(roundState = RoundState.Incorrect(heard, expected)) }
                tts?.speak(expected)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.release()
        tts?.release()
    }
}
