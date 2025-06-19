package com.ethyllium.productservice.infrastructure.persistence.entity

import com.ethyllium.productservice.infrastructure.web.rest.dto.response.WarehouseStockResponse
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "warehouses")
data class WarehouseStockDocument(
    @Indexed val warehouseName: String, val quantity: Int, val reservedQuantity: Int = 0, val location: String? = null
) {
    fun toResponse() = WarehouseStockResponse(
        warehouseName = warehouseName,
        quantity = quantity,
        reservedQuantity = reservedQuantity,
        location = location
    )
}