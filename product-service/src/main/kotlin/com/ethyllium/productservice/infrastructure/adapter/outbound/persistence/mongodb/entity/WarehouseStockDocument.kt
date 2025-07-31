package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.WarehouseStockResponse
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "warehouses")
data class WarehouseStockDocument(
    @Id var warehouseId: String? = null,
    @Indexed val warehouseName: String,
    val quantity: Int,
    val reservedQuantity: Int = 0,
    val location: String? = null
) {
    fun toResponse() = WarehouseStockResponse(
        warehouseName = warehouseName, quantity = quantity, reservedQuantity = reservedQuantity, location = location
    )

    fun toDomain(): WarehouseStock = WarehouseStock(
        warehouseName = this.warehouseName,
        quantity = this.quantity,
        reservedQuantity = this.reservedQuantity,
        location = this.location
    )
}

fun WarehouseStock.toDocument(): WarehouseStockDocument = WarehouseStockDocument(
    warehouseId = this.warehouseId,
    warehouseName = this.warehouseName,
    quantity = this.quantity,
    reservedQuantity = this.reservedQuantity,
    location = this.location
)