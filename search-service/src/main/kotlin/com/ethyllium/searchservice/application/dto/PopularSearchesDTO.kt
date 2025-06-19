package com.ethyllium.searchservice.application.dto

data class PopularSearchesDTO(
    val trends: List<PopularSearchItemDTO>
) {
    companion object {
        fun fromDomain(popularSearches: List<Pair<String, Int>>): PopularSearchesDTO {
            return PopularSearchesDTO(
                trends = popularSearches.map { (query, count) ->
                    PopularSearchItemDTO(query, count)
                }
            )
        }
    }
}