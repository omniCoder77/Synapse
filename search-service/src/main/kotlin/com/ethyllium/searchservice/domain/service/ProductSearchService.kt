package com.ethyllium.searchservice.domain.service

import com.ethyllium.searchservice.application.dto.SuggestionItemDTO
import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductSearchService {
    fun searchProducts(
        query: String?,
        category: String?,
        brand: List<String>?,
        minPrice: Double?,
        maxPrice: Double?,
        status: List<String>?,
        tags: List<String>?,
        color: List<String>?,
        pageable: Pageable,
    ): Page<SearchProduct>

    fun autocompleteSuggestions(term: String, limit: Int): List<SuggestionItemDTO>
    fun getSearchFacets(
        query: String?,
        filters: String?,
        category: List<String>?,
        brand: List<String>?,
        minPrice: Long?,
        maxPrice: Long?,
        status: List<String>?,
        tags: List<String>?,
        color: List<String>?,
        pageable: Pageable
    ): Map<String, Any>
    fun getProductById(id: String): Product?
    fun findSimilarProducts(productId: String, limit: Int): List<Product>
    fun getPopularSearches(limit: Int, timespan: String): List<Pair<String, Int>>
}