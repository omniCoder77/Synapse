package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.TaxClass
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.BulkPricingRequest
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.ProductPricingDocument
import java.time.Instant

data class ProductPricingRequest(
    val basePrice: Double,
    val salePrice: Double = basePrice,
    val costPrice: Double? = null,
    val currency: String = "USD",

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