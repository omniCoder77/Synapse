package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.domain.entity.ProductStatus
import com.ethyllium.productservice.domain.entity.ProductVisibility
import com.ethyllium.productservice.infrastructure.persistence.entity.ProductDocument
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

/**
 * The requesting client will be considered as seller
 */

data class CreateProductRequest(
    @field:NotBlank(message = "Product name is required") @field:Size(
        min = 2, max = 255, message = "Product name must be between 2 and 255 characters"
    ) val name: String,
    @field:NotBlank(message = "Product description is required") @field:Size(
        min = 10, max = 5000, message = "Product description must be between 10 and 5000 characters"
    ) val description: String,
    @field:Size(
        max = 500, message = "Short description must not exceed 500 characters"
    ) val shortDescription: String? = null,
    @field:NotBlank(message = "SKU is required") @field:Pattern(
        regexp = "^[A-Z0-9][A-Z0-9-_]{2,19}$", message = "SKU must contain only uppercase letters, numbers, hyphens, and underscores"
    ) val sku: String,
    @field:Pattern(regexp = "^[0-9]{8,15}$", message = "Barcode must be 8-15 digits") val barcode: String? = null,
    @field:NotBlank(message = "Brand ID is required") val brandName: String,
    val categoryName: String,
    val categoryPath: String,
    @field:Valid @field:NotNull(message = "Pricing information is required") val pricing: ProductPricingRequest,
    @field:Valid val inventory: ProductInventoryRequest = ProductInventoryRequest(),
    @field:Valid val specifications: ProductSpecificationsRequest = ProductSpecificationsRequest(),
    @field:Valid val media: ProductMediaRequest = ProductMediaRequest(),
    @field:Valid @field:NotNull(message = "SEO information is required") val seo: ProductSEORequest,
    @field:Valid val shipping: ProductShippingRequest = ProductShippingRequest(),
    val tags: Set<String> = emptySet(),
    val status: ProductStatus = ProductStatus.DRAFT,
    val visibility: ProductVisibility = ProductVisibility.PRIVATE,
    val reviewsEnabled: Boolean = true,
    val variantOfId: String = "",
    val searchTerms: List<String> = emptyList(),
    val facets: Map<String, Any> = emptyMap(),
    val variantCode: String
) {
    fun toDocument(sellerId: String) = ProductDocument(
        name = name,
        description = description,
        shortDescription = shortDescription,
        sku = sku,
        categoryName = categoryName,
        categoryPath = categoryPath,
        barcode = barcode,
        brandName = brandName,
        sellerId = sellerId,
        inventory = inventory.toDocument(),
        specifications = specifications.toDocument(),
        media = media.toDocument(),
        shipping = shipping.toDocument(),
        seo = seo.toDocument(),
        tags = tags,
        averageRating = 0,
        pricing = pricing.toDocument(),
        updatedAt = Instant.now(),
        productStatus = status.name,
        productVisibility = visibility.name,
        searchTerms = searchTerms,
        facets = facets,
        variantCode = variantCode,
    )
}