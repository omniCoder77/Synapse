package com.ethyllium.searchservice.domain.service

import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.infrastructure.adapter.outbound.elasticsearch.entity.SearchProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
    ): Mono<Page<SearchProduct>>


    fun getProductById(id: String): Mono<Product>
    fun findSimilarProducts(productId: String, limit: Int): Flux<Product>
    fun getPopularSearches(limit: Int, timespan: String): Flux<Pair<String, Int>>
}