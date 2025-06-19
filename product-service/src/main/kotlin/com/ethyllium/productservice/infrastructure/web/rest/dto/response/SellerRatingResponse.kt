package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class SellerRatingResponse(
    val averageRating: Long, val totalRatings: Int, val ratingDistribution: Map<Int, Int>, val badges: List<String>
)