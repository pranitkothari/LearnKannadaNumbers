package com.learnkannadanumbers.app.data

/**
 * The 47 basic Kannada letters (aksharagalu): 13 vowels (swaragalu) + 34
 * consonants (vyanjanagalu - 25 vargiya/structured + 9 avargiya/unstructured).
 * Excludes conjunct letters (ottakshara) and the two yogavaahakas (anusvara ಂ,
 * visarga ಃ). Cross-checked against multiple sources for both the letter set
 * and the retroflex-vs-dental grouping.
 *
 * Romanization marks retroflex consonants with a capital first letter (Ta,
 * Tha, Da, Dha, Na, La, Sha) to distinguish them from their dental/palatal
 * counterparts (ta, tha, da, dha, na, la, sha) - this is purely a display aid;
 * pronunciation matching always compares the Kannada text, never the
 * romanization.
 */
object KannadaAlphabet {

    // Kannada script to (romanization, isVowel)
    private val vowels = listOf(
        "ಅ" to "a",
        "ಆ" to "aa",
        "ಇ" to "i",
        "ಈ" to "ii",
        "ಉ" to "u",
        "ಊ" to "uu",
        "ಋ" to "ru",
        "ಎ" to "e",
        "ಏ" to "ee",
        "ಐ" to "ai",
        "ಒ" to "o",
        "ಓ" to "oo",
        "ಔ" to "au",
    )

    // 25 vargiya (structured) consonants, five varga rows of five.
    private val vargiyaConsonants = listOf(
        "ಕ" to "ka", "ಖ" to "kha", "ಗ" to "ga", "ಘ" to "gha", "ಙ" to "nga",
        "ಚ" to "cha", "ಛ" to "chha", "ಜ" to "ja", "ಝ" to "jha", "ಞ" to "nya",
        "ಟ" to "Ta", "ಠ" to "Tha", "ಡ" to "Da", "ಢ" to "Dha", "ಣ" to "Na",
        "ತ" to "ta", "ಥ" to "tha", "ದ" to "da", "ಧ" to "dha", "ನ" to "na",
        "ಪ" to "pa", "ಫ" to "pha", "ಬ" to "ba", "ಭ" to "bha", "ಮ" to "ma",
    )

    // 9 avargiya (unstructured) consonants.
    private val avargiyaConsonants = listOf(
        "ಯ" to "ya",
        "ರ" to "ra",
        "ಲ" to "la",
        "ವ" to "va",
        "ಶ" to "sha",
        "ಷ" to "Sha",
        "ಸ" to "sa",
        "ಹ" to "ha",
        "ಳ" to "La",
    )

    fun items(): List<PracticeItem> {
        val all = vowels + vargiyaConsonants + avargiyaConsonants
        return all.mapIndexed { index, (kannada, roman) ->
            PracticeItem(
                id = "letter:$index",
                kannada = kannada,
                transliteration = roman,
                displayLabel = kannada,
            )
        }
    }
}
