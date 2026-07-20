package com.learnkannadanumbers.app.data

/** Unifies every domain's items so Review mode can look any of them up by id. */
object PracticeCatalog {

    fun allItems(): List<PracticeItem> =
        KannadaNumbers.items() + KannadaAlphabet.items() + KannadaWords.allItems()

    fun findById(id: String): PracticeItem? = allItems().find { it.id == id }
}
