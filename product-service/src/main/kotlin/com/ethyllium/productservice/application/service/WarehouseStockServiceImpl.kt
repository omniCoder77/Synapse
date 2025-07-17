// src/main/kotlin/com/ethyllium/productservice/application/service/WarehouseStockServiceImpl.kt
package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driven.WarehouseStockRepository
import com.ethyllium.productservice.domain.port.driver.WarehouseStockService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class WarehouseStockServiceImpl(
    private val warehouseStockRepository: WarehouseStockRepository, private val eventPublisher: EventPublisher
) : WarehouseStockService {
    override fun create(warehouseStock: WarehouseStock): Mono<WarehouseStock> {
        return warehouseStockRepository.insert(warehouseStock).doOnSuccess { stock ->
            eventPublisher.publishWarehouseStockCreated(stock).subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    override fun update(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Boolean> {
        return warehouseStockRepository.update(warehouseId, quantity, reservedQuantity, location)
            .doOnSuccess { updated ->
                if (updated) {
                    eventPublisher.publishWarehouseStockUpdated(warehouseId, quantity, reservedQuantity, location)
                        .subscribeOn(Schedulers.boundedElastic()).subscribe()
                }
            }
    }

    override fun delete(warehouseId: String): Mono<Boolean> {
        return warehouseStockRepository.delete(warehouseId).doOnSuccess { deleted ->
            if (deleted) {
                eventPublisher.publishWarehouseStockDeleted(warehouseId).subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
        }
    }
}