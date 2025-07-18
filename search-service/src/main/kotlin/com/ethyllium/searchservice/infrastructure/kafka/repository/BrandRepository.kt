package com.ethyllium.searchservice.infrastructure.kafka.repository

import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface BrandRepository: ReactiveElasticsearchRepository<SearchProduct.SearchBrand, String> {
    fun findSearchBrandById(id: String): Mono<SearchProduct.SearchBrand>
}