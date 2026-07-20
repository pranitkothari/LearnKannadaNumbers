package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnkannadanumbers.app.data.KannadaWords
import com.learnkannadanumbers.app.data.PracticeItem

@Composable
fun WordsCategoryScreen(
    onSelectCategory: (KannadaWords.Category) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            ScreenTopBar("Words", onBack)

            Column(modifier = Modifier.padding(top = 16.dp)) {
                KannadaWords.Category.entries.forEach { category ->
                    Card(
                        onClick = { onSelectCategory(category) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                    ) {
                        Text(
                            text = category.label,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordsPracticeScreen(
    category: KannadaWords.Category,
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
                .padding(16.dp),
        ) {
            ScreenTopBar(category.label, onBack)

            val item = uiState.currentItem
            if (item == null) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(KannadaWords.itemsFor(category)) { word ->
                        Card(
                            onClick = { onSelectItem(word) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Text(
                                text = word.displayLabel,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(16.dp),
                            )
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
                    Text(text = item.displayLabel, fontSize = 32.sp)
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
                        Text("Choose another word")
                    }
                }
            }
        }
    }
}
