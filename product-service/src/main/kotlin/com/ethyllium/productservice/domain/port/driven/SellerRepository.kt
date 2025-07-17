package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.Seller
import reactor.core.publisher.Mono

interface SellerRepository {
    fun insert(seller: Seller): Mono<Seller>
    fun update(sellerId: String, businessName: String?, displayName: String?, phone: String?): Mono<Boolean>
    fun delete(sellerId: String): Mono<Boolean>
}