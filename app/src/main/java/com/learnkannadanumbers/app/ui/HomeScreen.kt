package com.learnkannadanumbers.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
        ) {
            Text(
                text = "ಕನ್ನಡ ಕಲಿ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Choose what to practice",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            ModeCard(
                title = "🔢 Numbers",
                subtitle = "Type a number 0-100, say it in Kannada",
                onClick = { onNavigate(Screen.NumbersPractice) },
            )
            ModeCard(
                title = "🔤 Alphabet",
                subtitle = "47 letters - vowels and consonants",
                onClick = { onNavigate(Screen.AlphabetPractice) },
            )
            ModeCard(
                title = "💬 Words",
                subtitle = "Colors, days, family, greetings, food",
                onClick = { onNavigate(Screen.WordsCategoryPicker) },
            )
            ModeCard(
                title = "🎯 Review",
                subtitle = "Quiz me on what I struggle with",
                onClick = { onNavigate(Screen.Review) },
            )
        }
    }
}

@Composable
private fun ModeCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
