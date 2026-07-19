package com.learnkannadanumbers.app.speech

/**
 * Lenient similarity check between what Whisper transcribed and the expected
 * Kannada word. Whisper-tiny on a lower-resource language like Kannada will
 * have transcription noise even on a correctly pronounced word, so we accept
 * matches that are "close enough" rather than requiring an exact match.
 */
object FuzzyMatch {

    private const val SIMILARITY_THRESHOLD = 0.6

    fun isMatch(heard: String, expected: String): Boolean {
        val a = normalize(heard)
        val b = normalize(expected)
        if (a.isEmpty() || b.isEmpty()) return false
        if (a == b) return true
        return similarity(a, b) >= SIMILARITY_THRESHOLD
    }

    private fun normalize(s: String): String =
        s.trim().lowercase().filter { !it.isWhitespace() }

    private fun similarity(a: String, b: String): Double {
        val distance = levenshtein(a, b)
        val maxLen = maxOf(a.length, b.length)
        if (maxLen == 0) return 1.0
        return 1.0 - distance.toDouble() / maxLen
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost,
                )
            }
        }
        return dp[a.length][b.length]
    }
}
