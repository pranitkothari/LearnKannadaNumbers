package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnkannadanumbers.app.data.KannadaWords
import com.learnkannadanumbers.app.data.PracticeItem

@Composable
fun MainScreen(
    uiState: MainUiState,
    hasMicPermission: Boolean,
    onNumberInputChanged: (String) -> Unit,
    onSelectItem: (PracticeItem?) -> Unit,
    onMicTapped: () -> Unit,
    onRequestMicPermission: () -> Unit,
    onDismissCrash: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateHome: () -> Unit,
    onOpenWordsCategory: (KannadaWords.Category) -> Unit,
    onStartReview: () -> Unit,
    onReviewNext: () -> Unit,
    canListen: Boolean,
) {
    if (uiState.lastCrash != null) {
        CrashReportScreen(crashText = uiState.lastCrash, onDismiss = onDismissCrash)
        return
    }

    if (uiState.modelLoadError != null || !uiState.modelsReady) {
        LoadingOrErrorScreen(uiState.modelLoadError)
        return
    }

    if (!hasMicPermission) {
        PermissionScreen(onRequestMicPermission)
        return
    }

    when (val screen = uiState.screen) {
        Screen.Home -> HomeScreen(onNavigate = { target ->
            if (target == Screen.Review) onStartReview() else onNavigate(target)
        })
        Screen.NumbersPractice -> NumbersScreen(
            uiState = uiState,
            canListen = canListen,
            onNumberInputChanged = onNumberInputChanged,
            onMicTapped = onMicTapped,
            onBack = onNavigateHome,
        )
        Screen.AlphabetPractice -> AlphabetScreen(
            uiState = uiState,
            canListen = canListen,
            onSelectItem = onSelectItem,
            onMicTapped = onMicTapped,
            onBack = onNavigateHome,
        )
        Screen.WordsCategoryPicker -> WordsCategoryScreen(
            onSelectCategory = onOpenWordsCategory,
            onBack = onNavigateHome,
        )
        is Screen.WordsPractice -> WordsPracticeScreen(
            category = screen.category,
            uiState = uiState,
            canListen = canListen,
            onSelectItem = onSelectItem,
            onMicTapped = onMicTapped,
            onBack = { onNavigate(Screen.WordsCategoryPicker) },
        )
        Screen.Review -> ReviewScreen(
            uiState = uiState,
            canListen = canListen,
            onMicTapped = onMicTapped,
            onNext = onReviewNext,
            onBack = onNavigateHome,
        )
    }
}

@Composable
private fun LoadingOrErrorScreen(modelLoadError: String?) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "ಕನ್ನಡ ಕಲಿ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            if (modelLoadError != null) {
                Text(
                    text = "Couldn't load offline speech models:\n$modelLoadError",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
                Text(text = "Loading offline speech models...", modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
private fun PermissionScreen(onRequestMicPermission: () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "ಕನ್ನಡ ಕಲಿ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onRequestMicPermission, modifier = Modifier.padding(top = 24.dp)) {
                Text("Grant microphone access")
            }
        }
    }
}

@Composable
private fun CrashReportScreen(crashText: String, onDismiss: () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Text(
                text = "The app crashed last time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Screenshot this and send it over.",
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
            SelectionContainer(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = crashText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text("Dismiss and continue")
            }
        }
    }
}
