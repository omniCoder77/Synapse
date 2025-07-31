package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.TempSellerDocument
import reactor.core.publisher.Mono

interface TempSellerRepository {
    fun findById(sellerId: String): Mono<TempSellerDocument>
    fun delete(sellerId: String): Mono<Boolean>
    fun findBySellerId(sellerId: String): Mono<TempSellerDocument>
}