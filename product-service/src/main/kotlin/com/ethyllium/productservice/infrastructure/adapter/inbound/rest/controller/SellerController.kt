package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.model.SellerStatus
import com.ethyllium.productservice.domain.port.driver.SellerService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.RegisterSellerRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateSellerRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/seller")
class SellerController(
    private val sellerService: SellerService
) {
    @GetMapping("/{sellerId}")
    fun getSellerById(@PathVariable sellerId: String): Mono<ResponseEntity<ApiResponse<Seller>>> {
        return sellerService.getById(sellerId).map { seller -> ResponseEntity.ok(ApiResponse.success(seller)) }
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found")))
    }

    @PatchMapping("/{sellerId}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateSeller(
        @PathVariable sellerId: String,
        @RequestBody updateSellerRequest: UpdateSellerRequest,
        authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        // Ensure a seller can only update their own profile
        if (sellerId != authentication.name) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access Denied")))
        }
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
        ).map { updated ->
            if (updated) ResponseEntity.ok(ApiResponse.success("Seller updated successfully"))
            else ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Seller not found or no changes were made."))
        }
    }

    @DeleteMapping("/{sellerId}")
    @PreAuthorize("hasRole('SELLER') OR hasRole('ADMIN')")
    fun deleteSeller(
        @PathVariable sellerId: String, authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return sellerService.delete(sellerId).map { deleted ->
            if (deleted) ResponseEntity.ok(ApiResponse.success("Seller deleted successfully"))
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found."))
        }
    }

    @PostMapping("/{sellerId}/verifications/phone/initiate")
    @PreAuthorize("hasRole('SELLER')")
    fun initiatePhoneVerification(
        @PathVariable sellerId: String, @RequestParam phoneNumber: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return sellerService.initiatePhoneVerification(sellerId, phoneNumber).map { sent ->
            if (sent) ResponseEntity.ok(ApiResponse.success("Phone verification OTP sent successfully."))
            else ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to send OTP."))
        }
    }

    @PostMapping("/{sellerId}/verifications/phone/complete")
    @PreAuthorize("hasRole('SELLER')")
    fun completePhoneVerification(
        @PathVariable sellerId: String, @RequestParam code: String, @RequestParam phoneNumber: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return sellerService.updatePhoneNumber(
            sellerId, code, phoneNumber
        ).map { updated ->
            if (updated) ResponseEntity.ok(ApiResponse.success("Phone number verified and updated successfully."))
            else ResponseEntity.badRequest().body(ApiResponse.error("Invalid OTP or phone number."))
        }
    }

    @PostMapping("/verifications/email/initiate")
    @PreAuthorize("hasRole('SELLER')")
    fun initiateEmailVerification(
        authentication: Authentication, @RequestParam email: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        sellerService.initiateEmailVerification(authentication.name, email)
        return Mono.just(ResponseEntity.ok(ApiResponse.success("Email verification initiated. Please check your inbox.")))
    }

    @PostMapping("/verifications/email/complete")
    @PreAuthorize("hasRole('SELLER')")
    fun completeEmailVerification(
        @RequestParam token: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return sellerService.updateEmail(token).map { updated ->
            if (updated) ResponseEntity.ok(ApiResponse.success("Email verified successfully."))
            else ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired token."))
        }
    }

    @PatchMapping("/{sellerId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateStatus(
        @PathVariable sellerId: String, @RequestParam status: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return sellerService.update(sellerId, status = SellerStatus.valueOf(status.uppercase())).map { updated ->
            if (updated) ResponseEntity.ok(ApiResponse.success("Seller status updated successfully"))
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found."))
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('SELLER')")
    fun registerSeller(
        @RequestBody registerSellerRequest: RegisterSellerRequest, authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<Seller>>> {
        val sellerId = authentication.name
        return sellerService.registerSeller(registerSellerRequest, sellerId).map { createdSeller ->
            val location = URI.create("/api/v1/products/seller/${createdSeller.id}")
            ResponseEntity.created(location).body(ApiResponse.success(createdSeller, "Seller registered successfully."))
        }.onErrorResume { e ->
            Mono.just(
                ResponseEntity.badRequest().body(ApiResponse.error(e.message ?: "Failed to register seller."))
            )
        }
    }
}