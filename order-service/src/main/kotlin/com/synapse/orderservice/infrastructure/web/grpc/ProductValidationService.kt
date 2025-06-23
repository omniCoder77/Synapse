package com.synapse.orderservice.infrastructure.web.grpc

import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationRequest
import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationServiceGrpc
import com.ethyllium.productservice.infrastructure.web.grpc.ProductsRequest
import com.synapse.orderservice.domain.model.Money
import com.synapse.orderservice.domain.model.OrderItem
import com.synapse.orderservice.domain.model.ProductId
import com.synapse.orderservice.infrastructure.web.rest.dto.IdAmount
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ProductValidationService(
    @Value("\${product-service.name}") private val productServiceName: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    val channel: ManagedChannel = ManagedChannelBuilder.forTarget(productServiceName).usePlaintext().build()
    val productService: ProductValidationServiceGrpc.ProductValidationServiceBlockingStub =
        ProductValidationServiceGrpc.newBlockingStub(channel)

    fun createProduct(products: List<IdAmount>): Pair<List<OrderItem>, Double> {
        // Fix: Build the request properly
        val validateProductRequestBuilder = ProductValidationRequest.newBuilder()

        products.forEach { product ->
            val productRequest = ProductsRequest.newBuilder().setId(product.id).setQuantity(product.amount)
                .build() // Build each ProductsRequest

            validateProductRequestBuilder.addProductRequest(productRequest)
        }

        val validateProductRequest = validateProductRequestBuilder.build()

        try {
            val productValidationResponse = productService.validate(validateProductRequest)
            var total = 0.0

            val orderItems = productValidationResponse.productResponseList.map { productResponse ->
                total += productResponse.quantity
                OrderItem(
                    productId = ProductId(productResponse.id),
                    quantity = productResponse.quantity,
                    unitPrice = Money(productResponse.amount * productResponse.quantity),
                    name = productResponse.name,
                    imageUrl = productResponse.imageUrl,
                    tax = Money(productResponse.tax),
                    discount = Money(productResponse.discount)
                )
            }
            return Pair(orderItems, total)
        } catch (e: StatusRuntimeException) {
            logger.error("gRPC Status: ${e.status}")
            logger.error("gRPC Status Code: ${e.status.code}")
            logger.error("gRPC Description: ${e.status.description}")
            logger.error("gRPC Cause: ${e.status.cause}")
            throw e
        } catch (e: Exception) {
            logger.error("gRPC call failed: ${e.message}")
            throw e
        }
    }
}