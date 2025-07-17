package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.domain.port.driven.WarehouseStockRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.WarehouseStockDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDocument
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WarehouseStockRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) :
    WarehouseStockRepository {
    override fun insert(warehouseStock: WarehouseStock): Mono<WarehouseStock> {
        return reactiveMongoTemplate.insert(warehouseStock.toDocument()).map { it.toDomain() }
    }

    override fun update(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Boolean> {
        val update = Update()
        quantity?.let { update.set("quantity", it) }
        reservedQuantity?.let { update.set("reservedQuantity", it) }
        location?.let { update.set("location", it) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("id").`is`(warehouseId)), update, WarehouseStockDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun delete(warehouseId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("id").`is`(warehouseId)), WarehouseStockDocument::class.java
        ).map { it.wasAcknowledged() }
    }
}