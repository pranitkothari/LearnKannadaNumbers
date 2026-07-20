package com.learnkannadanumbers.app.ui

import com.learnkannadanumbers.app.data.KannadaWords

sealed interface Screen {
    data object Home : Screen
    data object NumbersPractice : Screen
    data object AlphabetPractice : Screen
    data object WordsCategoryPicker : Screen
    data class WordsPractice(val category: KannadaWords.Category) : Screen
    data object Review : Screen
}
