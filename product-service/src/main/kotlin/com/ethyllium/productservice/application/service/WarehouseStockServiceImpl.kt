package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.domain.port.driven.OutboxEntityRepository
import com.ethyllium.productservice.domain.port.driven.WarehouseStockRepository
import com.ethyllium.productservice.domain.port.driver.WarehouseStockService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class WarehouseStockServiceImpl(
    private val warehouseStockRepository: WarehouseStockRepository, private val outboxEntityRepository: OutboxEntityRepository
) : WarehouseStockService {
    override fun create(warehouseStock: WarehouseStock): Mono<WarehouseStock> {
        return warehouseStockRepository.insert(warehouseStock).flatMap { createdStock ->
            outboxEntityRepository.publishWarehouseStockCreated(createdStock)
                .thenReturn(createdStock)
        }
    }

    override fun update(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Boolean> {
        return warehouseStockRepository.update(warehouseId, quantity, reservedQuantity, location)
            .flatMap { updated ->
                if (updated) {
                    outboxEntityRepository.publishWarehouseStockUpdated(warehouseId, quantity, reservedQuantity, location)
                        .thenReturn(true)
                } else {
                    Mono.just(false)
                }
            }
    }

    override fun delete(warehouseId: String): Mono<Boolean> {
        return warehouseStockRepository.delete(warehouseId).flatMap { deleted ->
            if (deleted) {
                outboxEntityRepository.publishWarehouseStockDeleted(warehouseId).thenReturn(true)
            } else {
                Mono.just(false)
            }
        }
    }
}