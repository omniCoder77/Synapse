package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import java.time.LocalDateTime

data class ProductAnalyticsResponse(
    val views: Long,
    val clicks: Long,
    val conversions: Long,
    val wishlistAdds: Long,
    val cartAdds: Long,
    val lastViewedAt: LocalDateTime?
)
