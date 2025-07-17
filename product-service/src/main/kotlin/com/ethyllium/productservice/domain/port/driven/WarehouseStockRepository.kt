package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.WarehouseStock
import reactor.core.publisher.Mono

interface WarehouseStockRepository {
    fun insert(warehouseStock: WarehouseStock): Mono<WarehouseStock>
    fun update(warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?): Mono<Boolean>
    fun delete(warehouseId: String): Mono<Boolean>
}