package com.ethyllium.searchservice.infrastructure.elasticsearch.repository

import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchProductRepository: ElasticsearchRepository<SearchProduct, String> {
}