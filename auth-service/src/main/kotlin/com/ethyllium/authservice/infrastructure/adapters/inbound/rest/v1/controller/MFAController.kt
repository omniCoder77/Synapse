package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.ApiResponse
import com.ethyllium.authservice.application.service.UserService
import com.ethyllium.authservice.application.service.ValidationService
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.TotpSecretGenerator
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.UserEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class MFAController(
    private val userService: UserService,
    private val validationService: ValidationService,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val tokenService: TokenService,
    private val qrCodeGenerator: QrCodeGenerator
) {

    companion object {
        const val MFA_VERIFIED = "mfa_verified"
    }

    @GetMapping("/setup-2fa")
    fun setup2fa(@AuthenticationPrincipal userEntity: UserEntity): Mono<ResponseEntity<ByteArray>> {
        val (secret, otpUri) = totpSecretGenerator.generateTotpSecret(userEntity.email)
        return userService.updateUserSecret(userEntity.username, secret).flatMap {
            val qrCode = qrCodeGenerator.generateQrCode(otpUri)
            Mono.just(ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode))
        }
    }

    @PostMapping("/verify-mfa")
    fun verify2fa(
        @RequestParam code: String, @AuthenticationPrincipal userEntity: UserEntity
    ): ResponseEntity<ApiResponse> {
        val isValid = validationService.validateCode(userEntity.totp!!, code)
        return if (isValid) {
            val accessToken = tokenService.generateAccessToken(
                subject = userEntity.username.toString(), additionalClaims = mapOf(MFA_VERIFIED to true)
            )
            return ResponseEntity.ok().headers { it.add(HttpHeaders.AUTHORIZATION, accessToken) }
                .body(ApiResponse.Success("MFA authentication successful"))
        } else {
            ResponseEntity.badRequest().body(ApiResponse.Success("Invalid code"))
        }
    }
}