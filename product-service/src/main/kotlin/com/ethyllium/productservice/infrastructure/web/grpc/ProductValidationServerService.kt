package com.ethyllium.productservice.infrastructure.web.grpc

import com.ethyllium.productservice.domain.entity.StockStatus
import com.ethyllium.productservice.infrastructure.persistence.repository.ProductRepository
import org.springframework.grpc.server.service.GrpcService
import kotlin.jvm.optionals.getOrNull

@GrpcService
class ProductValidationServerService(private val productRepository: ProductRepository) :
    ProductValidationServiceGrpcKt.ProductValidationServiceCoroutineImplBase() {

    override suspend fun validate(request: ProductValidationRequest): ProductValidationResponse {
        val productValidationResponse = ProductValidationResponse.newBuilder()

        request.productRequestList.forEach { productRequest ->
            val productResponse = ProductResponse.newBuilder()
            val product = productRepository.findById(productRequest.id).getOrNull()

            if (product == null) {
                productResponse.setDoesExist(false).setIsInStock(false)
            } else {
                productResponse.setAmount(product.pricing.basePrice).setDoesExist(true).setIsInStock(
                    product.inventory.stockStatus == StockStatus.IN_STOCK.name || product.inventory.stockStatus == StockStatus.LOW_STOCK.name
                ).setId(productRequest.id).setQuantity(productRequest.quantity)
                    .setImageUrl(product.media.primaryImageUrl).setName(product.name)
                    .setDiscount(product.pricing.discountPercentage).setTax(product.pricing.taxClass.percentage)
            }
            productValidationResponse.addProductResponse(productResponse.build())
        }
        return productValidationResponse.build()
    }
}