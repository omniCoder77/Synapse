package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.domain.entity.TaxClass
import com.ethyllium.productservice.infrastructure.persistence.entity.ProductPricingDocument
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.Instant

data class ProductPricingRequest(
    @field:NotNull(message = "Base price is required") @field:DecimalMin(
        value = "0.01",
        message = "Base price must be greater than 0"
    ) @field:Digits(integer = 10, fraction = 2, message = "Invalid price format") val basePrice: Double,

    @field:DecimalMin(value = "0.01", message = "Sale price must be greater than 0") @field:Digits(
        integer = 10,
        fraction = 2,
        message = "Invalid price format"
    ) val salePrice: Double = basePrice,

    @field:DecimalMin(value = "0.01", message = "Cost price must be greater than 0") @field:Digits(
        integer = 10,
        fraction = 2,
        message = "Invalid price format"
    ) val costPrice: Double? = null,

    @field:Pattern(
        regexp = "^[A-Z]{3}$",
        message = "Currency must be a valid 3-letter ISO code"
    ) val currency: String = "USD",

    val taxClass: TaxClass = TaxClass.EXEMPT,
    val taxIncluded: Boolean = false,
    val bulkPricing: List<BulkPricingRequest> = emptyList(),
    val priceValidFrom: Long? = null,
    val priceValidTo: Long? = null
) {
    fun toDocument() = ProductPricingDocument(
        basePrice = basePrice,
        salePrice = salePrice,
        costPrice = costPrice,
        currency = currency,
        taxClass = taxClass,
        taxIncluded = taxIncluded,
        priceValidFrom = priceValidFrom?.let { Instant.ofEpochMilli(it) },
        priceValidTo = priceValidTo?.let { Instant.ofEpochMilli(it) },
        bulkPricing = bulkPricing.map { it.toDocument() },
    )
}