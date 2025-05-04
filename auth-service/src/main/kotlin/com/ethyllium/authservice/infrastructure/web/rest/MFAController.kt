package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.service.UserService
import com.ethyllium.authservice.application.service.ValidationService
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.TotpSecretGenerator
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.infrastructure.persistence.jpa.UserEntity
import com.ethyllium.authservice.mfa.MfaPurposeFactory
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class MFAController(
    private val userService: UserService,
    private val validationService: ValidationService,
    private val mfaPurposeFactory: MfaPurposeFactory,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val tokenService: TokenService,
    private val qrCodeGenerator: QrCodeGenerator
) {
    @GetMapping("/setup-2fa")
    fun setup2fa(@AuthenticationPrincipal userEntity: UserEntity): ResponseEntity<ByteArray> {
        val (secret, otpUri) = totpSecretGenerator.generateTotpSecret(userEntity.email)
        userService.updateUserSecret(userEntity.username, secret)
        val qrCode = qrCodeGenerator.generateQrCode(otpUri)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode)
    }

    @PostMapping("/verify-2fa")
    fun verify2fa(
        @RequestParam code: String,
        @AuthenticationPrincipal userEntity: UserEntity,
        @RequestParam(HttpHeaders.AUTHORIZATION) token: String
    ): ResponseEntity<String> {
        val purpose = tokenService.getClaims(token.removePrefix("Bearer "))?.get(Claims.MFA_ACTION) as String?
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val mfaPurpose = MfaPurpose.get(purpose)
        val isValid = validationService.validateCode(userEntity.totp!!, code)
        return if (isValid) {
            val mfaResult = mfaPurposeFactory.getMfaPurposeHandler(mfaPurpose)
                .handle(username = userEntity.username, userId = userEntity.username)
            return ResponseEntity.ok().headers { it.add(HttpHeaders.AUTHORIZATION, mfaResult) }
                .body("MFA authentication successful")
        } else {
            ResponseEntity.badRequest().body("Invalid code")
        }
    }
}