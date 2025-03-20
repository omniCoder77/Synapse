package com.ethyllium.searchservice.search

import java.util.*

class BrandExtractor(private val brands: List<String>, private val threshold: Double = 0.8) {

    // Normalizes text: lowercases, trims whitespace, and removes non-alphanumeric characters.
    private fun normalize(text: String): String {
        return text.lowercase(Locale.getDefault()).trim().replace(Regex("[^a-z0-9\\s]"), "")
    }

    /**
     * Extracts the best matching brand from the query.
     *
     * The algorithm:
     * 1. Normalizes the query.
     * 2. Checks if any normalized brand is an exact substring of the query.
     * 3. If not, iterates over each brand and uses a sliding window to compare substrings
     *    (of the same length as the brand) to compute a fuzzy similarity score.
     * 4. Returns the brand if a similarity above the threshold is found.
     */
    fun extractBrand(query: String): String? {
        val normalizedQuery = normalize(query)

        for (brand in brands) {
            if (normalizedQuery.contains(normalize(brand))) {
                return brand
            }
        }

        var bestMatch: String? = null
        var bestScore = 0.0

        for (brand in brands) {
            val normalizedBrand = normalize(brand)
            val brandLength = normalizedBrand.length
            if (brandLength == 0 || normalizedQuery.length < brandLength) continue

            for (i in 0..(normalizedQuery.length - brandLength)) {
                val sub = normalizedQuery.substring(i, i + brandLength)
                val score = similarity(sub, normalizedBrand)
                if (score > bestScore) {
                    bestScore = score
                    bestMatch = brand
                }
            }
        }
        return if (bestScore >= threshold) bestMatch else null
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) {
            dp[i][0] = i
        }
        for (j in 0..b.length) {
            dp[0][j] = j
        }
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,    // deletion
                    dp[i][j - 1] + 1,    // insertion
                    dp[i - 1][j - 1] + cost  // substitution
                )
            }
        }
        return dp[a.length][b.length]
    }

    private fun similarity(a: String, b: String): Double {
        val maxLength = maxOf(a.length, b.length)
        if (maxLength == 0) return 1.0
        val distance = levenshtein(a, b)
        return (maxLength - distance).toDouble() / maxLength
    }
}