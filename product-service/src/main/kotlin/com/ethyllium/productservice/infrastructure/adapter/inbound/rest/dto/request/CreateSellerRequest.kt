package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.*

data class CreateSellerRequest(
    val businessName: String,
    val displayName: String,
    val email: String,
    val phone: String?,
    val address: Address,
    val businessInfo: BusinessInfo,
    val policies: SellerPolicies,
    val bankDetails: BankDetails?,
    val taxInfo: TaxInfo
) {
    fun toSeller(id: String? = null) = Seller(
        id = id ?: email,
        businessName = businessName,
        displayName = displayName,
        email = email,
        phone = phone,
        address = address,
        businessInfo = businessInfo,
        sellerRating = SellerRating(),
        policies = policies,
        bankDetails = bankDetails,
        taxInfo = taxInfo
    )
}