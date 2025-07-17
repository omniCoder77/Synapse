package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.StockStatus

data class ProductInventoryResponse(
    val stockQuantity: Int,
    val reservedQuantity: Int,
    val availableQuantity: Int,
    val lowStockThreshold: Int,
    val outOfStockThreshold: Int,
    val backorderAllowed: Boolean,
    val preorderAllowed: Boolean,
    val stockStatus: StockStatus,
    val warehouseLocations: List<WarehouseStockResponse>,
    val lastStockUpdate: Long
)
