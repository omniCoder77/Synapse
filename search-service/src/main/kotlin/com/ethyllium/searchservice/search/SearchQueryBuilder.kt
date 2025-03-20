package com.ethyllium.searchservice.search

import com.ethyllium.searchservice.dto.request.SearchRequest

class SearchQueryBuilder {
    companion object {
        fun getSearchRequest(query: String): SearchRequest {
            return SearchRequest(
            title = "Mr",
            category = "Electronics",
            range = 187..188,
            description = "Mauris enim leo"
        )
        }
    }
}