package com.ethyllium.searchservice.service

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.Result
import co.elastic.clients.elasticsearch.core.DeleteRequest
import com.ethyllium.searchservice.dto.request.SearchRequest
import com.ethyllium.searchservice.model.OutboxEvent
import com.ethyllium.searchservice.model.Product
import com.ethyllium.searchservice.ports.DeleteProduct
import com.ethyllium.searchservice.ports.EventFailed
import com.ethyllium.searchservice.ports.EventProcessed
import com.ethyllium.searchservice.ports.InsertProduct
import com.ethyllium.searchservice.repository.OutboxRepository
import com.ethyllium.searchservice.util.DatabaseConstants
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class ProductService(
    private val elasticsearchClient: ElasticsearchClient, private val outboxRepository: OutboxRepository
) : InsertProduct, DeleteProduct, EventProcessed, EventFailed {

    private val logger = LoggerFactory.getLogger(this::class.java)

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

    override fun insert(product: Product): Product? {
        val response = elasticsearchClient.index {
            it.index(DatabaseConstants.PRODUCT_INDEX_NAME).id(product.id).document(product)
        }

        if (response.result() == Result.Created || response.result() == Result.Updated) {
            return product
        } else {
            logger.error("Failed to upsert product with ID: ${product.id}")
            return null
        }
    }

    override fun deleteProduct(productId: String) {
        val deleteRequest = DeleteRequest.Builder().index(DatabaseConstants.PRODUCT_INDEX_NAME).id(productId).build()
        val res = elasticsearchClient.delete(deleteRequest)
        if (res.result() != Result.Deleted) {
            throw IllegalStateException("Failed to delete product with ID: $productId")
        }
    }

    override fun processedEvents(event: OutboxEvent) {
        outboxRepository.delete(event)
    }

    override fun eventFailed(event: OutboxEvent) {
        outboxRepository.save(event)
    }
}