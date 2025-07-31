package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.*

data class RegisterSellerRequest(
    val businessName: String,
    val address: Address,
    val businessInfo: BusinessInfo,
    val sellerRating: SellerRating,
    val policies: SellerPolicies,
    val bankDetails: BankDetails? = null,
    val taxInfo: TaxInfo,
    val status: String,
    val verificationStatus: String,
)