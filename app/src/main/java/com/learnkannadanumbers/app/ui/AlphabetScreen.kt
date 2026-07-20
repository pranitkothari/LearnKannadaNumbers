package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnkannadanumbers.app.data.KannadaAlphabet
import com.learnkannadanumbers.app.data.PracticeItem

@Composable
fun AlphabetScreen(
    uiState: MainUiState,
    canListen: Boolean,
    onSelectItem: (PracticeItem?) -> Unit,
    onMicTapped: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
        ) {
            ScreenTopBar("Alphabet", onBack)

            val item = uiState.currentItem
            if (item == null) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                ) {
                    items(KannadaAlphabet.items()) { letter ->
                        Card(
                            onClick = { onSelectItem(letter) },
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = letter.kannada, fontSize = 22.sp)
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = item.displayLabel, fontSize = 56.sp)
                    MicButton(
                        canListen = canListen,
                        roundState = uiState.roundState,
                        onClick = onMicTapped,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                    FeedbackArea(uiState.roundState)
                    TextButton(
                        onClick = { onSelectItem(null) },
                        modifier = Modifier.padding(top = 24.dp),
                    ) {
                        Text("Choose another letter")
                    }
                }
            }
        }
    }
}
