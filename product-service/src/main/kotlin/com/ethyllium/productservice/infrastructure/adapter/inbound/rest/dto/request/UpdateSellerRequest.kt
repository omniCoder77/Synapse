package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.*

data class UpdateSellerRequest(
    val businessName: String? = null,
    val displayName: String? = null,
    val address: Address? = null,
    val businessInfo: BusinessInfo? = null,
    val sellerRating: SellerRating? = null,
    val policies: SellerPolicies? = null,
    val bankDetails: BankDetails? = null,
    val taxInfo: TaxInfo? = null,
)