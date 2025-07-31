package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.ProductInventoryDocument
import java.time.Instant

data class ProductInventoryRequest(
    val trackInventory: Boolean = true,
    val stockQuantity: Int = 0,
    val reservedQuantity: Int = 0,
    val lowStockThreshold: Int = 10,
    val outOfStockThreshold: Int = 0,
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