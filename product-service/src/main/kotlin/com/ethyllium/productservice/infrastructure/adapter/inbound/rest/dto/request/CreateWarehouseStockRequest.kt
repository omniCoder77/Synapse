package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request
import com.ethyllium.productservice.domain.model.WarehouseStock

data class CreateWarehouseStockRequest(
    val warehouseName: String,
    val quantity: Int,
    val reservedQuantity: Int = 0,
    val location: String? = null
) {
    fun toWarehouseStock() = WarehouseStock(
        warehouseName = warehouseName,
        quantity = quantity,
        reservedQuantity = reservedQuantity,
        location = location
    )
}