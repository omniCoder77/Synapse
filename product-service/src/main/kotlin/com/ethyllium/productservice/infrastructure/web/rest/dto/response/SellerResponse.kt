package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.SellerStatus
import com.ethyllium.productservice.domain.entity.VerificationStatus

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