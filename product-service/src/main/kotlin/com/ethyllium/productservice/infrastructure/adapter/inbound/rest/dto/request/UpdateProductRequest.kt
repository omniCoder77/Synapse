package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.ProductDocument
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateProductRequest(
    @field:Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    val name: String? = null,

    @field:Size(min = 10, max = 5000, message = "Product description must be between 10 and 5000 characters")
    val description: String? = null,

    @field:Size(max = 500, message = "Short description must not exceed 500 characters")
    val shortDescription: String? = null,

    @field:Pattern(regexp = "^[0-9]{8,15}$", message = "Barcode must be 8-15 digits")
    val barcode: String? = null,

    val brandId: String? = null,
    val categoryId: String? = null,

    @field:Valid
    val pricing: ProductPricingRequest? = null,

    @field:Valid
    val inventory: ProductInventoryRequest? = null,

    @field:Valid
    val specifications: ProductSpecificationsRequest? = null,

    @field:Valid
    val media: ProductMediaRequest? = null,

    @field:Valid
    val seo: ProductSEORequest? = null,

    @field:Valid
    val shipping: ProductShippingRequest? = null,

    val tags: Set<String>? = null,
    val status: ProductStatus? = null,
    val visibility: ProductVisibility? = null,

    @field:Valid
    val sku: String? = null,
) {
    fun updateDocument(existingProduct: ProductDocument) {

    }
}