package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

data class UpdateWarehouseStockRequest(
    val quantity: Int?,
    val reservedQuantity: Int?,
    val location: String?
)