package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

class UpdateBrandRequest(
    val name: String? = null,
    val description: String? = null,
    val website: String? = null,
    val slug: String? = null,
)