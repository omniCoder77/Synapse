package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class WarehouseStockDeletedEvent(val warehouseId: String) : Event