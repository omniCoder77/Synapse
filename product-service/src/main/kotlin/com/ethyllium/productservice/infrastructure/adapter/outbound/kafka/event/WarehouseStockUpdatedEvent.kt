package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class WarehouseStockUpdatedEvent(
    val warehouseId: String,
    val quantity: Int?,
    val reservedQuantity: Int?,
    val location: String?
) : Event