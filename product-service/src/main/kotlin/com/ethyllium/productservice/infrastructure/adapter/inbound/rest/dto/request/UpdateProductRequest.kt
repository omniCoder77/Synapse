package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.ProductMediaRequest

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val barcode: String? = null,
    val brandId: String? = null,
    val categoryId: String? = null,
    val pricing: ProductPricingRequest? = null,
    val inventory: ProductInventoryRequest? = null,
    val specifications: ProductSpecificationsRequest? = null,
    val media: ProductMediaRequest? = null,
    val seo: ProductSEORequest? = null,
    val shipping: ProductShippingRequest? = null,
    val tags: Set<String>? = null,
    val status: ProductStatus? = null,
    val visibility: ProductVisibility? = null,
    val sku: String? = null,
)