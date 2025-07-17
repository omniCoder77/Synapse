package com.ethyllium.productservice.infrastructure.adapter.inbound.grpc

import com.ethyllium.productservice.domain.model.StockStatus
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.ProductDocument
import com.ethyllium.productservice.infrastructure.web.grpc.ProductResponse
import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationRequest
import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationResponse
import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationServiceGrpcKt
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class ProductValidationServerService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ProductValidationServiceGrpcKt.ProductValidationServiceCoroutineImplBase() {

    override suspend fun validate(request: ProductValidationRequest): ProductValidationResponse {
        val requestedIds = request.productRequestList.map { it.id }
        val idToQuantity = request.productRequestList.associateBy({ it.id }, { it.quantity })

        val objectIds = requestedIds.mapNotNull {
            try {
                ObjectId(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        val products = reactiveMongoTemplate.find(
            Query.query(Criteria.where("_id").`in`(objectIds)), ProductDocument::class.java
        ).collectList().awaitSingle()

        val productMap = products!!.associateBy { it.id!!.toHexString() }

        val responseBuilder = ProductValidationResponse.newBuilder()

        requestedIds.forEach { reqId ->
            val productResponse = ProductResponse.newBuilder()
            val product = productMap[reqId]

            if (product == null) {
                productResponse.setDoesExist(false).setIsInStock(false)
            } else {
                val quantity = idToQuantity[reqId] ?: 0
                productResponse.setId(reqId).setQuantity(quantity).setName(product.name)
                    .setImageUrl(product.media.primaryImageUrl).setAmount(product.pricing.basePrice)
                    .setTax(product.pricing.taxClass.percentage).setDiscount(product.pricing.discountPercentage)
                    .setDoesExist(true).setIsInStock(
                        product.inventory.stockStatus == StockStatus.IN_STOCK.name || product.inventory.stockStatus == StockStatus.LOW_STOCK.name
                    )
            }
            responseBuilder.addProductResponse(productResponse.build())
        }

        return responseBuilder.build()
    }
}
