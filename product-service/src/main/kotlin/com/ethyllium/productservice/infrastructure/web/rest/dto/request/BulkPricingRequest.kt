package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.infrastructure.persistence.entity.BulkPricingDocument
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class BulkPricingRequest(
    @field:Min(value = 1, message = "Minimum quantity must be at least 1") val minQuantity: Int,

    @field:Min(value = 1, message = "Maximum quantity must be at least 1") val maxQuantity: Int? = null,

    @field:NotNull(message = "Price is required") @field:DecimalMin(
        value = "0.01",
        message = "Price must be greater than 0"
    ) val price: Long,

    @field:DecimalMin(value = "0", message = "Discount percentage cannot be negative") @field:DecimalMax(
        value = "100",
        message = "Discount percentage cannot exceed 100"
    ) val discountPercentage: Long? = null
) {
    fun toDocument() = BulkPricingDocument(
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        price = price,
        discountPercentage = discountPercentage
    )
}