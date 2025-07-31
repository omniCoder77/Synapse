package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.Brand

data class CreateBrandRequest(
    val name: String,
    val description: String? = null,
    val website: String? = null,
    val slug: String,
) {
    fun toBrand(id: String? = null, logoUrl: String? = null, ownerId: String) = Brand(
        id = id, name = name, description = description, logoUrl = logoUrl, website = website, slug = slug, ownerId = ownerId
    )
}