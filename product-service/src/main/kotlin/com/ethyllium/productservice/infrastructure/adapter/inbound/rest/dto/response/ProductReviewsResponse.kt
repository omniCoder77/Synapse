package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import java.time.LocalDateTime

data class ProductReviewsResponse(
    val reviewsEnabled: Boolean,
    val averageRating: Long,
    val totalReviews: Int,
    val ratingDistribution: Map<Int, Int>,
    val lastReviewDate: LocalDateTime?
)
