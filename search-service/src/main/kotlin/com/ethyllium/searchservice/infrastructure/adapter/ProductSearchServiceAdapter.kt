package com.ethyllium.searchservice.infrastructure.adapter

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import com.ethyllium.searchservice.application.dto.SuggestionItemDTO
import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.domain.service.ProductSearchService
import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component


@Component
class ProductSearchServiceAdapter(
    private val elasticsearchClient: ElasticsearchClient
) : ProductSearchService {

    override fun searchProducts(
        query: String?,
        category: String?,
        brand: List<String>?,
        minPrice: Double?,
        maxPrice: Double?,
        status: List<String>?,
        tags: List<String>?,
        color: List<String>?,
        pageable: Pageable,
    ): Page<SearchProduct> {
        val response = elasticsearchClient.search({
            it.index("products")
            it.from(pageable.offset.toInt())
            it.size(pageable.pageSize)

            // Add sorting support
            if (pageable.sort.isSorted) {
                pageable.sort.forEach { order ->
                    it.sort { sortBuilder ->
                        sortBuilder.field { fieldSort ->
                            fieldSort.field(order.property)
                            fieldSort.order(
                                if (order.isAscending) SortOrder.Asc
                                else SortOrder.Desc
                            )
                        }
                    }
                }
            }

            it.query { queryBuilder ->
                queryBuilder.bool { boolQuery ->
                    if (maxPrice != null || minPrice != null) {
                        boolQuery.filter { filterQuery ->
                            filterQuery.range { rangeQuery ->
                                rangeQuery.term {
                                    if (maxPrice != null) it.lte(maxPrice.toString())
                                    if (minPrice != null) it.gte(minPrice.toString())
                                    it.field("pricing.basePrice")
                                }
                            }
                        }
                    }
                    category?.let { cat ->
                        boolQuery.filter { filterQuery ->
                            filterQuery.term { termQuery ->
                                termQuery.field("category.name.keyword").value(cat)
                            }
                        }
                    }

                    boolQuery.must { mustQuery ->
                        mustQuery.multiMatch { multiMatchQuery ->
                            multiMatchQuery.query(query).fields("name^2", "description^1.5", "shortDescription^1")
                                .type(TextQueryType.BestFields)
                        }
                    }
                }
            }
        }, SearchProduct::class.java)

        val content = response.hits().hits().mapNotNull { it.source() }
        val totalElements = response.hits().total()?.value() ?: 0L

        return PageImpl(content, pageable, totalElements)
    }

    override fun autocompleteSuggestions(
        term: String, limit: Int
    ): List<SuggestionItemDTO> {
        TODO("Not yet implemented")
    }

    override fun getSearchFacets(
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
    ): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Product? {
        TODO("Not yet implemented")
    }

    override fun findSimilarProducts(
        productId: String, limit: Int
    ): List<Product> {
        TODO("Not yet implemented")
    }

    override fun getPopularSearches(
        limit: Int, timespan: String
    ): List<Pair<String, Int>> {
        TODO("Not yet implemented")
    }
}