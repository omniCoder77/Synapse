package com.synapse.orderservice.infrastructure.inbound.grpc.grpc

import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationRequest
import com.ethyllium.productservice.infrastructure.web.grpc.ProductValidationServiceGrpc
import com.ethyllium.productservice.infrastructure.web.grpc.ProductsRequest
import com.synapse.orderservice.domain.model.OrderId
import com.synapse.orderservice.domain.model.OrderItem
import com.synapse.orderservice.domain.model.ProductId
import com.synapse.orderservice.infrastructure.inbound.rest.rest.dto.IdQuantity
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class ProductValidationService(
    @Value("\${product-service.name}") private val productServiceName: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val channel: ManagedChannel =
        ManagedChannelBuilder.forTarget(productServiceName)
            .usePlaintext()
            .build()

    private val asyncStub: ProductValidationServiceGrpc.ProductValidationServiceStub =
        ProductValidationServiceGrpc.newStub(channel)

    fun createProduct(products: List<IdQuantity>): Mono<Pair<List<OrderItem>, Double>> {
        val validateProductRequestBuilder = ProductValidationRequest.newBuilder()

        products.forEach { product ->
            val productRequest = ProductsRequest.newBuilder()
                .setId(product.id)
                .setQuantity(product.amount)
                .build()

            validateProductRequestBuilder.addProductRequest(productRequest)
        }

        val validateProductRequest = validateProductRequestBuilder.build()
        return Mono.just(Pair(listOf(OrderItem(
            productId = ProductId(UUID.randomUUID()),
            quantity = 10,
            unitPrice = 110.0,
            orderId = OrderId(UUID.randomUUID())
        )), 0.0))
//        return Mono.create { sink ->
//            asyncStub.validate(validateProductRequest, object : StreamObserver<ProductValidationResponse> {
//                override fun onNext(response: ProductValidationResponse) {
//                    var total = 0.0
//
//                    val orderItems = response.productResponseList.map { productResponse ->
//                        total += productResponse.quantity
//                        OrderItem(
//                            productId = ProductId(UUID.fromString(productResponse.id)),
//                            quantity = productResponse.quantity,
//                            unitPrice = productResponse.amount * productResponse.quantity,
//                            orderId = OrderId(UUID.randomUUID())
//                        )
//                    }
//
//                    sink.success(Pair(orderItems, total))
//                }
//
//                override fun onError(t: Throwable) {
//                    logger.error("gRPC call failed", t)
//                    sink.error(t)
//                }
//
//                override fun onCompleted() {
//                    // no-op
//                }
//            })
//        }
    }
}
