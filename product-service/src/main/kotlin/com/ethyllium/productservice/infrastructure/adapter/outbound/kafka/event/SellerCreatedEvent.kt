package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.productservice.domain.model.*

data class SellerCreatedEvent(
    val id: String,
    val businessName: String,
    val displayName: String,
    val email: String,
    val phone: String?,
    val address: Address,
    val businessInfo: BusinessInfo,
    val sellerRating: SellerRating,
    val policies: SellerPolicies,
    val bankDetails: BankDetails?,
    val taxInfo: TaxInfo
) : Event