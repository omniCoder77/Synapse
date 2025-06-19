package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.infrastructure.persistence.entity.DropShippingInfoDocument
import jakarta.validation.constraints.DecimalMin

data class DropShippingInfoRequest(
    val enabled: Boolean = false, val supplierId: String? = null, val supplierSku: String? = null,

    @field:DecimalMin(value = "0.01", message = "Supplier price must be greater than 0") val supplierPrice: Long? = null
) {
    fun toDocument() = DropShippingInfoDocument(
        enabled = enabled, supplierId = supplierId, supplierSku = supplierSku, supplierPrice = supplierPrice
    )
}