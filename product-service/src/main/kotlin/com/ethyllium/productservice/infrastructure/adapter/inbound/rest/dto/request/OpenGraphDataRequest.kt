package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.OpenGraphDataDocument

data class OpenGraphDataRequest(
    val title: String, val description: String, val image: String? = null,

    val type: String = "product"
) {
    fun toDocument() = OpenGraphDataDocument(
        title = title, description = description, image = image, type = type
    )
}