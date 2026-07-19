package com.learnkannadanumbers.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KannadaOrange = Color(0xFFC2410C)

private val LightColors = lightColorScheme(primary = KannadaOrange)
private val DarkColors = darkColorScheme(primary = KannadaOrange)

@Composable
fun LearnKannadaNumbersTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
