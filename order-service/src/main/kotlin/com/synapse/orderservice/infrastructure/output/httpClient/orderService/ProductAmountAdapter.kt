package com.synapse.orderservice.infrastructure.output.httpClient.orderService

import com.synapse.orderservice.domain.port.driven.ProductAmount
import org.springframework.stereotype.Component

@Component
class ProductAmountAdapter(private val productAmountClient: ProductAmountClient) : ProductAmount {
    override fun calculateAmount(productId: String): Double? {
        return productAmountClient.calculatePrice(productId)
    }
}