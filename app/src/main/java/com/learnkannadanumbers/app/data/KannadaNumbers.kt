package com.learnkannadanumbers.app.data

/**
 * Kannada number words for 0-100.
 *
 * 0-19, the decade words (20/30/.../90) and 100 are irregular and listed explicitly.
 * 21-99 are built from a tens stem + a fixed unit suffix, e.g.
 * ಇಪ್ಪತ್ತು (20) + ಒಂದು (1) -> ಇಪ್ಪತ್ತೊಂದು (21).
 * This sandhi pattern was cross-checked against several known compounds
 * (21, 22, 23, 32, 45, 57) and applied uniformly across all ten decades.
 * The programmatically generated 21-99 forms have not been individually
 * confirmed by a native speaker - worth a spot-check before relying on them
 * to teach pronunciation.
 */
object KannadaNumbers {

    private val units = arrayOf(
        "ಸೊನ್ನೆ", // 0
        "ಒಂದು", // 1
        "ಎರಡು", // 2
        "ಮೂರು", // 3
        "ನಾಲ್ಕು", // 4
        "ಐದು", // 5
        "ಆರು", // 6
        "ಏಳು", // 7
        "ಎಂಟು", // 8
        "ಒಂಬತ್ತು", // 9
    )

    private val teens = arrayOf(
        "ಹನ್ನೊಂದು", // 11
        "ಹನ್ನೆರಡು", // 12
        "ಹದಿಮೂರು", // 13
        "ಹದಿನಾಲ್ಕು", // 14
        "ಹದಿನೈದು", // 15
        "ಹದಿನಾರು", // 16
        "ಹದಿನೇಳು", // 17
        "ಹದಿನೆಂಟು", // 18
        "ಹತ್ತೊಂಬತ್ತು", // 19
    )

    // index = tens digit (2-9); pair = (stem used before a unit suffix, full decade word)
    private val decades = mapOf(
        2 to ("ಇಪ್ಪ" to "ಇಪ್ಪತ್ತು"),
        3 to ("ಮೂವ" to "ಮೂವತ್ತು"),
        4 to ("ನಲ್ವ" to "ನಲ್ವತ್ತು"),
        5 to ("ಐವ" to "ಐವತ್ತು"),
        6 to ("ಅರವ" to "ಅರವತ್ತು"),
        7 to ("ಎಪ್ಪ" to "ಎಪ್ಪತ್ತು"),
        8 to ("ಎಂಬ" to "ಎಂಬತ್ತು"),
        9 to ("ತೊಂಬ" to "ತೊಂಬತ್ತು"),
    )

    // index = units digit (1-9), appended to "<stem>ತ್ತ" to form the compound word
    private val unitSuffix = arrayOf(
        "", // 0 - unused, decade word is used directly
        "ೊಂದು", // 1
        "ೆರಡು", // 2
        "ಮೂರು", // 3
        "ನಾಲ್ಕು", // 4
        "ೈದು", // 5
        "ಾರು", // 6
        "ೇಳು", // 7
        "ೆಂಟು", // 8
        "ೊಂಬತ್ತು", // 9
    )

    fun wordFor(n: Int): String {
        require(n in 0..100) { "Number out of supported range (0-100): $n" }
        return when {
            n <= 9 -> units[n]
            n == 10 -> "ಹತ್ತು"
            n <= 19 -> teens[n - 11]
            n == 100 -> "ನೂರು"
            n % 10 == 0 -> decades.getValue(n / 10).second
            else -> {
                val (stem, _) = decades.getValue(n / 10)
                stem + "ತ್ತ" + unitSuffix[n % 10]
            }
        }
    }

    // Romanized (English-letter) spelling, parallel to the tables above. The
    // compound-word suffixes already include the vowel-sandhi elision from the
    // "tta" join point (e.g. unit 1 is "ondu" -> stem + "tt" + "ondu" = "...ttondu",
    // matching the same elision as the Kannada script's vowel-sign merge).

    private val unitsRoman = arrayOf(
        "sonne", "ondu", "eradu", "muru", "nalku", "aidu", "aru", "elu", "entu", "ombattu",
    )

    private val teensRoman = arrayOf(
        "hannondu", "hanneradu", "hadimuru", "hadinalku", "hadinaidu",
        "hadinaru", "hadinelu", "hadinentu", "hattombattu",
    )

    private val decadesRoman = mapOf(
        2 to ("ippa" to "ippattu"),
        3 to ("muva" to "muvattu"),
        4 to ("nalva" to "nalvattu"),
        5 to ("aiva" to "aivattu"),
        6 to ("arava" to "aravattu"),
        7 to ("eppa" to "eppattu"),
        8 to ("emba" to "embattu"),
        9 to ("tomba" to "tombattu"),
    )

    private val unitSuffixRoman = arrayOf(
        "", "ondu", "eradu", "amuru", "analku", "aidu", "aru", "elu", "entu", "ombattu",
    )

    fun transliterationFor(n: Int): String {
        require(n in 0..100) { "Number out of supported range (0-100): $n" }
        return when {
            n <= 9 -> unitsRoman[n]
            n == 10 -> "hattu"
            n <= 19 -> teensRoman[n - 11]
            n == 100 -> "nooru"
            n % 10 == 0 -> decadesRoman.getValue(n / 10).second
            else -> {
                val (stem, _) = decadesRoman.getValue(n / 10)
                stem + "tt" + unitSuffixRoman[n % 10]
            }
        }
    }

    fun items(): List<PracticeItem> = (0..100).map { n ->
        PracticeItem(
            id = "number:$n",
            kannada = wordFor(n),
            transliteration = transliterationFor(n),
            displayLabel = n.toString(),
        )
    }
}
