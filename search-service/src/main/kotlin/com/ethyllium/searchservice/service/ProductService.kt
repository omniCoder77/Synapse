package com.ethyllium.searchservice.service

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.IndexResponse
import com.ethyllium.searchservice.dto.request.SearchRequest
import com.ethyllium.searchservice.model.Product
import com.ethyllium.searchservice.util.DatabaseConstants
import org.springframework.stereotype.Service


@Service
class ProductService(
    private val elasticsearchClient: ElasticsearchClient
) {

    fun add(product: Product): IndexResponse? {
        return elasticsearchClient.index {
            it.index(DatabaseConstants.PRODUCT_INDEX_NAME).id(product.id).document(product)
        }
    }

    fun search(searchRequest: SearchRequest, brand: String?): List<Product> {
        val searchResponse = elasticsearchClient.search({
            it.index("products").query {
                it.bool {
                    it.must {
                        searchRequest.title?.let { title ->
                            it.match { it.field(Product::name.name).query(title) }
                        }

                        searchRequest.description?.let { description ->
                            it.match { it.field(Product::description.name).query(description) }
                        }

                        searchRequest.category?.let { category ->
                            it.term { it.field(Product::category.name).value(category) }
                        }

                        brand?.let { brand ->
                            it.term { it.field(Product::brand.name).value(brand) }
                        }

                        searchRequest.range?.let { range ->
                            it.range {
                                it.number { it.gte(range.first.toDouble()).lte(range.last.toDouble()) }
                            }
                        }
                    }
                }
            }
        }, Product::class.java)
        return searchResponse.hits().hits().mapNotNull { it.source() }
    }
}