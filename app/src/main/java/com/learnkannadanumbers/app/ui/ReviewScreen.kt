package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReviewScreen(
    uiState: MainUiState,
    canListen: Boolean,
    onMicTapped: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            ScreenTopBar("Review", onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val item = uiState.currentItem
                when {
                    !uiState.reviewStarted -> CircularProgressIndicator()
                    uiState.reviewQueue.isEmpty() -> {
                        Text(text = "Nothing to review yet!", fontSize = 20.sp)
                        Text(
                            text = "Practice a few numbers, letters, or words first - " +
                                "I'll quiz you here on whatever you've struggled with.",
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    item != null -> {
                        Text(text = "${uiState.reviewIndex + 1} / ${uiState.reviewQueue.size}")
                        Text(
                            text = item.displayLabel,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                        )
                        MicButton(canListen = canListen, roundState = uiState.roundState, onClick = onMicTapped)
                        FeedbackArea(uiState.roundState)
                        if (uiState.roundState is RoundState.Correct || uiState.roundState is RoundState.Incorrect) {
                            Button(onClick = onNext, modifier = Modifier.padding(top = 24.dp)) {
                                Text("Next")
                            }
                        }
                    }
                }
            }
        }
    }
}
