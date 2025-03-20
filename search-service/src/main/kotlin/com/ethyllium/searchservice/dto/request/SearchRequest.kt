package com.ethyllium.searchservice.dto.request

data class SearchRequest(
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val range: IntRange? = null
)
