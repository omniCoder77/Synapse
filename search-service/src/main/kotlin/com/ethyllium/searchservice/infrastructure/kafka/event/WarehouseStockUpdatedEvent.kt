package com.ethyllium.searchservice.infrastructure.kafka.event

data class WarehouseStockUpdatedEvent(
    val warehouseId: String,
    val quantity: Int?,
    val reservedQuantity: Int?,
    val location: String?
)