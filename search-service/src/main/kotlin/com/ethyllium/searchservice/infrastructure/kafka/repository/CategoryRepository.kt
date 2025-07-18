package com.ethyllium.searchservice.infrastructure.kafka.repository

import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CategoryRepository: ReactiveElasticsearchRepository<SearchProduct.SearchCategory, String> {
    fun findSearchCategoryById(id: String): Mono<SearchProduct.SearchCategory>
}