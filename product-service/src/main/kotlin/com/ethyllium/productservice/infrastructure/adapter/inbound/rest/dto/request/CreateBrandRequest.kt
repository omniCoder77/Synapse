package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.BrandDocument

data class CreateBrandRequest(
    val name: String,
    val description: String? = null,
    val website: String? = null,
    val slug: String,
) {
    fun toBrandDocument() = BrandDocument(
        name = name, description = description, website = website, slug = slug
    )

    fun toBrand(id: String? = null, logoUrl: String? = null) = Brand(
        id = id, name = name, description = description, logoUrl = logoUrl, website = website, slug = slug
    )
}