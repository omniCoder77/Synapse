package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.WarehouseStock
import reactor.core.publisher.Mono

interface WarehouseStockService {
    fun create(warehouseStock: WarehouseStock): Mono<WarehouseStock>
    fun update(
        warehouseId: String,
        quantity: Int?,
        reservedQuantity: Int?,
        location: String?
    ): Mono<Boolean>
    fun delete(warehouseId: String): Mono<Boolean>
}