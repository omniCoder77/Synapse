package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.infrastructure.persistence.entity.WarehouseStockDocument
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class WarehouseStockRequest(
    @field:NotBlank(message = "Warehouse name is required") val warehouseName: String,

    @field:Min(value = 0, message = "Quantity cannot be negative") val quantity: Int,

    @field:Min(value = 0, message = "Reserved quantity cannot be negative") val reservedQuantity: Int = 0,

    val location: String? = null
) {
    fun toDocument() = WarehouseStockDocument(
        warehouseName = warehouseName,
        quantity = quantity,
        reservedQuantity = reservedQuantity,
        location = location
    )
}