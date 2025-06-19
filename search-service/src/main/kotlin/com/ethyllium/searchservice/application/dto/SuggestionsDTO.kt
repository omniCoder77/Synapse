package com.ethyllium.searchservice.application.dto

data class SuggestionsDTO(
    val suggestions: List<SuggestionItemDTO>
)

data class SuggestionItemDTO(
    val text: String,
    val highlight: String,
    val score: Double
)