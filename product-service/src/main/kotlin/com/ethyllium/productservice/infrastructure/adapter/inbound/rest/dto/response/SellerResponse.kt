package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.SellerStatus
import com.ethyllium.productservice.domain.model.VerificationStatus

data class SellerResponse(
    val id: String,
    val businessName: String,
    val displayName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val businessInfo: String?,
    val sellerRating: SellerRatingResponse,
    val policies: String?,
    val bankDetails: String?,
    val taxInfo: String?,
    val status: SellerStatus,
    val verificationStatus: VerificationStatus,
)