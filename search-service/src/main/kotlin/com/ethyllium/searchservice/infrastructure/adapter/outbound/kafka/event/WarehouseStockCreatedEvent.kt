package com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.event

data class WarehouseStockCreatedEvent(
    val warehouseId: String,
    val warehouseName: String,
    val quantity: Int,
    val reservedQuantity: Int,
    val location: String?
)