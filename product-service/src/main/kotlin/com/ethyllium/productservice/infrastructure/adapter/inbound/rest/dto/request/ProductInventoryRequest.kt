package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.WarehouseStockRequest
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.ProductInventoryDocument
import jakarta.validation.constraints.Min
import java.time.Instant

data class ProductInventoryRequest(
    val trackInventory: Boolean = true,

    @field:Min(value = 0, message = "Stock quantity cannot be negative") val stockQuantity: Int = 0,

    @field:Min(value = 0, message = "Reserved quantity cannot be negative") val reservedQuantity: Int = 0,

    @field:Min(value = 0, message = "Low stock threshold cannot be negative") val lowStockThreshold: Int = 10,

    @field:Min(value = 0, message = "Out of stock threshold cannot be negative") val outOfStockThreshold: Int = 0,

    val backorderAllowed: Boolean = false,
    val preorderAllowed: Boolean = false,
    val warehouseLocations: List<WarehouseStockRequest> = emptyList(),
    val stockStatus: String = ""
) {
    fun toDocument() = ProductInventoryDocument(
        stockQuantity = stockQuantity,
        reservedQuantity = reservedQuantity,
        lowStockThreshold = lowStockThreshold,
        outOfStockThreshold = outOfStockThreshold,
        backorderAllowed = backorderAllowed,
        preorderAllowed = preorderAllowed,
        warehouseLocations = warehouseLocations.map { it.toDocument() },
        stockStatus = stockStatus,
        lastStockUpdate = Instant.now()
    )
}