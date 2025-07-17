package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class WarehouseStockResponse(
    val warehouseName: String,
    val quantity: Int,
    val reservedQuantity: Int,
    val location: String?
)