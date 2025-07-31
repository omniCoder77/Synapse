package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.inbound.kafka.entities.SellerRegisteredEvent
import reactor.core.publisher.Mono

interface SellerRepository {
    fun insert(seller: Seller): Mono<Seller>
    fun update(
        sellerId: String,
        businessName: String? = null,
        displayName: String? = null,
        address: Address? = null,
        businessInfo: BusinessInfo? = null,
        sellerRating: SellerRating? = null,
        policies: SellerPolicies? = null,
        bankDetails: BankDetails? = null,
        taxInfo: TaxInfo? = null,
        status: SellerStatus?
    ): Mono<Boolean>

    fun updatePhoneNumber(sellerId: String, phoneNumber: String): Mono<Boolean>
    fun updateEmail(sellerId: String, email: String): Mono<Boolean>

    fun delete(sellerId: String): Mono<Boolean>
    fun findById(sellerId: String): Mono<Seller>
    fun addTempSeller(sellerRegisteredEvent: SellerRegisteredEvent)
}