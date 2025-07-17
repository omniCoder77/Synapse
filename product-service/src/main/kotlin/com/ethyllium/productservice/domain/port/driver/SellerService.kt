package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.Seller
import reactor.core.publisher.Mono

interface SellerService {
    fun create(seller: Seller): Mono<Seller>
    fun update(
        sellerId: String,
        businessName: String?,
        displayName: String?,
        phone: String?
    ): Mono<Boolean>
    fun delete(sellerId: String): Mono<Boolean>
}