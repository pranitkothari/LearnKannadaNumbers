package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    uiState: MainUiState,
    hasMicPermission: Boolean,
    onNumberInputChanged: (String) -> Unit,
    onMicTapped: () -> Unit,
    onRequestMicPermission: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "ಕನ್ನಡ ಸಂಖ್ಯೆಗಳು",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Learn Kannada numbers",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            when {
                uiState.modelLoadError != null -> {
                    Text(
                        text = "Couldn't load offline speech models:\n${uiState.modelLoadError}",
                        color = MaterialTheme.colorScheme.error,
                    )
                    return@Column
                }
                !uiState.modelsReady -> {
                    CircularProgressIndicator()
                    Text(text = "Loading offline speech models...", modifier = Modifier.padding(top = 16.dp))
                    return@Column
                }
            }

            OutlinedTextField(
                value = uiState.numberInput,
                onValueChange = onNumberInputChanged,
                label = { Text("Number (0-100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.inputError != null,
                supportingText = uiState.inputError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                modifier = Modifier.padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!hasMicPermission) {
                    Button(onClick = onRequestMicPermission) {
                        Text("Grant microphone access")
                    }
                } else {
                    val target = uiState.numberInput.toIntOrNull()
                    val canListen = target != null && target in 0..100 &&
                        uiState.roundState !is RoundState.Listening &&
                        uiState.roundState !is RoundState.Processing

                    Button(
                        onClick = onMicTapped,
                        enabled = canListen,
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = when (uiState.roundState) {
                                RoundState.Listening -> "  Listening..."
                                RoundState.Processing -> "  Checking..."
                                else -> "  Speak the number in Kannada"
                            },
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                FeedbackArea(uiState.roundState)
            }
        }
    }
}

@Composable
private fun FeedbackArea(roundState: RoundState) {
    when (roundState) {
        is RoundState.Correct -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = "Correct!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        is RoundState.Incorrect -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text(text = "Not quite - listen to the correct pronunciation", fontSize = 16.sp)
                Text(
                    text = "You said: ${roundState.heard.ifBlank { "(nothing heard)" }}",
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(text = "Correct: ${roundState.expected}")
            }
        }
        else -> Unit
    }
}
