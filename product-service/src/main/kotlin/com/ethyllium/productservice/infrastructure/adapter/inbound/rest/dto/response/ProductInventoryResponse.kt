package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

data class ProductInventoryResponse(
    val stockQuantity: Int,
    val reservedQuantity: Int,
    val availableQuantity: Int,
    val lowStockThreshold: Int,
    val outOfStockThreshold: Int,
    val backorderAllowed: Boolean,
    val preorderAllowed: Boolean,
    val stockStatus: String,
    val warehouseLocations: List<WarehouseStockResponse>,
    val lastStockUpdate: Long
)
