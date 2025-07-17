package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.domain.port.driver.WarehouseStockService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateWarehouseStockRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateWarehouseStockRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/warehouses")
class WarehouseStockController(
    private val warehouseStockService: WarehouseStockService
) {

    @PostMapping
    fun createWarehouseStock(@RequestBody createWarehouseStockRequest: CreateWarehouseStockRequest): Mono<ResponseEntity<WarehouseStock>> {
        return warehouseStockService.create(createWarehouseStockRequest.toWarehouseStock()).map { createdWarehouseStock ->
            ResponseEntity.created(URI.create("/api/v1/warehouses/${createdWarehouseStock.warehouseId}"))
                .body(createdWarehouseStock)
        }
    }

    @PatchMapping("/{warehouseId}")
    fun updateWarehouseStock(
        @PathVariable warehouseId: String,
        @RequestBody updateWarehouseStockRequest: UpdateWarehouseStockRequest
    ): Mono<ResponseEntity<String>> {
        return warehouseStockService.update(
            warehouseId,
            updateWarehouseStockRequest.quantity,
            updateWarehouseStockRequest.reservedQuantity,
            updateWarehouseStockRequest.location
        ).map { updatedWarehouseStock ->
            if (!updatedWarehouseStock) ResponseEntity.badRequest().body("Failed to update warehouse stock")
            else ResponseEntity.ok("Warehouse stock updated successfully")
        }
    }

    @DeleteMapping("/{warehouseId}")
    fun deleteWarehouseStock(@PathVariable warehouseId: String): Mono<ResponseEntity<String>> {
        return warehouseStockService.delete(warehouseId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete warehouse stock")
            else ResponseEntity.ok("Warehouse stock deleted successfully")
        }
    }
}