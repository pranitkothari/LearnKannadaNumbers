package com.learnkannadanumbers.app.data

/**
 * A single thing to practice pronouncing, shared across every domain
 * (numbers, alphabet, words). [id] must be stable and globally unique -
 * it's the primary key for progress tracking.
 */
data class PracticeItem(
    val id: String,
    val kannada: String,
    val transliteration: String,
    val displayLabel: String,
)

enum class PracticeDomain(val label: String) {
    NUMBERS("Numbers"),
    ALPHABET("Alphabet"),
    WORDS("Words"),
}
