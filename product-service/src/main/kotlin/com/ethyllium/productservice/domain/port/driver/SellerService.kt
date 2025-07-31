package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.RegisterSellerRequest
import reactor.core.publisher.Mono

interface SellerService {
    fun delete(sellerId: String): Mono<Boolean>
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
        status: SellerStatus? = null,
    ): Mono<Boolean>

    fun getById(sellerId: String): Mono<Seller>
    fun initiatePhoneVerification(sellerId: String, phoneNumber: String): Mono<Boolean>
    fun updatePhoneNumber(sellerId: String, code: String, phoneNumber: String): Mono<Boolean>
    fun initiateEmailVerification(sellerId: String, email: String)
    fun updateEmail(token: String): Mono<Boolean>
    fun registerSeller(registerSeller: RegisterSellerRequest, sellerId: String): Mono<Seller>
}