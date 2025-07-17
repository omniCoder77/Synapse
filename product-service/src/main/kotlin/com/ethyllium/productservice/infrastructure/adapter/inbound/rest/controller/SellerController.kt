package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.port.driver.SellerService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateSellerRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateSellerRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/sellers")
class SellerController(
    private val sellerService: SellerService
) {

    @PostMapping
    fun createSeller(@RequestBody createSellerRequest: CreateSellerRequest): Mono<ResponseEntity<Seller>> {
        return sellerService.create(createSellerRequest.toSeller()).map { createdSeller ->
            ResponseEntity.created(URI.create("/api/v1/sellers/${createdSeller.id}")).body(createdSeller)
        }
    }

    @PatchMapping("/{sellerId}")
    fun updateSeller(
        @PathVariable sellerId: String,
        @RequestBody updateSellerRequest: UpdateSellerRequest
    ): Mono<ResponseEntity<String>> {
        return sellerService.update(
            sellerId,
            updateSellerRequest.businessName,
            updateSellerRequest.displayName,
            updateSellerRequest.phone
        ).map { updatedSeller ->
            if (!updatedSeller) ResponseEntity.badRequest().body("Failed to update seller")
            else ResponseEntity.ok("Seller updated successfully")
        }
    }

    @DeleteMapping("/{sellerId}")
    fun deleteSeller(@PathVariable sellerId: String): Mono<ResponseEntity<String>> {
        return sellerService.delete(sellerId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete seller")
            else ResponseEntity.ok("Seller deleted successfully")
        }
    }
}