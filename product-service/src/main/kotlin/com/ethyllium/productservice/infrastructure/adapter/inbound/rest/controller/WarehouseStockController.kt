package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.WarehouseStock
import com.ethyllium.productservice.domain.port.driver.WarehouseStockService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateWarehouseStockRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateWarehouseStockRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/warehouses")
class WarehouseStockController(
    private val warehouseStockService: WarehouseStockService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createWarehouseStock(@RequestBody createWarehouseStockRequest: CreateWarehouseStockRequest): Mono<ResponseEntity<ApiResponse<WarehouseStock>>> {
        return warehouseStockService.create(createWarehouseStockRequest.toWarehouseStock()).map { createdWarehouseStock ->
            val location = URI.create("/api/v1/warehouses/${createdWarehouseStock.warehouseId}")
            ResponseEntity.created(location)
                .body(ApiResponse.success(createdWarehouseStock, "Warehouse created successfully."))
        }
    }

    @PatchMapping("/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateWarehouseStock(
        @PathVariable warehouseId: String,
        @RequestBody updateWarehouseStockRequest: UpdateWarehouseStockRequest
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return warehouseStockService.update(
            warehouseId,
            updateWarehouseStockRequest.quantity,
            updateWarehouseStockRequest.reservedQuantity,
            updateWarehouseStockRequest.location
        ).map { updated ->
            if (updated) ResponseEntity.ok(ApiResponse.success("Warehouse stock updated successfully"))
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Warehouse not found or no changes were made."))
        }
    }

    @DeleteMapping("/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteWarehouseStock(@PathVariable warehouseId: String): Mono<ResponseEntity<ApiResponse<String>>> {
        return warehouseStockService.delete(warehouseId).map { deleted ->
            if (deleted) ResponseEntity.ok(ApiResponse.success("Warehouse stock deleted successfully"))
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Warehouse not found."))
        }
    }
}