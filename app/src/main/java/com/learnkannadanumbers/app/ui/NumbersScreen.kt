package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NumbersScreen(
    uiState: MainUiState,
    canListen: Boolean,
    onNumberInputChanged: (String) -> Unit,
    onMicTapped: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            ScreenTopBar("Numbers", onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
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
                    MicButton(canListen = canListen, roundState = uiState.roundState, onClick = onMicTapped)
                    FeedbackArea(uiState.roundState)
                }
            }
        }
    }
}
