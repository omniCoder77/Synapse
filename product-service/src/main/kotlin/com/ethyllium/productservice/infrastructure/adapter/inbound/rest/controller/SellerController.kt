package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.model.SellerStatus
import com.ethyllium.productservice.domain.port.driver.SellerService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateSellerRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateSellerRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
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

    @GetMapping("/{sellerId}")
    fun getSellerById(@PathVariable sellerId: String): Mono<ResponseEntity<Seller>> {
        return sellerService.getById(sellerId).map { seller ->
            if (seller != null) ResponseEntity.ok(seller)
            else ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{sellerId}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateSeller(
        @PathVariable sellerId: String, @RequestBody updateSellerRequest: UpdateSellerRequest
    ): Mono<ResponseEntity<String>> {
        return sellerService.update(
            sellerId,
            updateSellerRequest.businessName,
            updateSellerRequest.displayName,
            updateSellerRequest.address,
            updateSellerRequest.businessInfo,
            updateSellerRequest.sellerRating,
            updateSellerRequest.policies,
            updateSellerRequest.bankDetails,
            updateSellerRequest.taxInfo
        ).map { updatedSeller ->
            if (!updatedSeller) ResponseEntity.badRequest().body("Failed to update seller")
            else ResponseEntity.ok("Seller updated successfully")
        }
    }

    @DeleteMapping("/{sellerId}")
    @PreAuthorize("hasRole('SELLER') OR hasRole('ADMIN')")
    fun deleteSeller(@PathVariable sellerId: String): Mono<ResponseEntity<String>> {
        return sellerService.delete(sellerId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete seller")
            else ResponseEntity.ok("Seller deleted successfully")
        }
    }

    @PostMapping("/{sellerId}/verifications/phone/initiate")
    @PreAuthorize("hasRole('SELLER')")
    fun initiatePhoneVerification(
        @PathVariable sellerId: String, @RequestParam phoneNumber: String
    ): Mono<ResponseEntity<String>> {
        return sellerService.initiatePhoneVerification(sellerId, phoneNumber).map { sendOtp ->
            if (sendOtp) ResponseEntity.ok("Phone verification initiated with ID: $sellerId")
            else ResponseEntity.badRequest().body("Failed to initiate phone verification")
        }
    }

    @PostMapping("/{sellerId}/verifications/phone/complete")
    @PreAuthorize("hasRole('SELLER')")
    fun updatePhoneNumber(
        @PathVariable sellerId: String, @RequestParam code: String, @RequestParam phoneNumber: String
    ): Mono<ResponseEntity<String>> {
        return sellerService.updatePhoneNumber(
            sellerId, code, phoneNumber
        ).map {
            if (it) ResponseEntity.ok("Phone verification initiated with ID: $sellerId")
            else ResponseEntity.badRequest().body("Failed to update phone number")
        }
    }

    @PostMapping("/verifications/email/initiate")
    @PreAuthorize("hasRole('SELLER')")
    fun initiateEmailVerification(
        authentication: Authentication, @RequestParam email: String
    ): ResponseEntity<String?> {
        sellerService.initiateEmailVerification(authentication.name, email)
        return ResponseEntity.ok("Email verification initiated with ID: ${authentication.name}")
    }

    @PostMapping("/verifications/email/complete")
    @PreAuthorize("hasRole('SELLER')")
    fun updateEmail(
        @RequestParam token: String
    ): Mono<ResponseEntity<String>> {
        return sellerService.updateEmail(token).map {
            if (it) ResponseEntity.ok("Email updated successfully")
            else ResponseEntity.badRequest().body("Failed to update email")
        }
    }

    @PatchMapping("/{sellerId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateStatus(@PathVariable sellerId: String, @RequestParam status: String): Mono<ResponseEntity<String>> {
        return sellerService.update(sellerId, status = SellerStatus.valueOf(status)).map { updated ->
            if (updated) ResponseEntity.ok("Seller status updated successfully")
            else ResponseEntity.badRequest().body("Failed to update seller status")
        }
    }
}