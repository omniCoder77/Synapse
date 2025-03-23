package com.ethyllium.authservice.api

import com.ethyllium.authservice.mfa.MfaPurposeFactory
import com.ethyllium.authservice.model.User
import com.ethyllium.authservice.service.*
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
    private val totpService: TotpService,
    private val qrCodeService: QrCodeService,
    private val userService: UserService,
    private val validationService: ValidationService,
    private val jwtService: JwtService,
    private val mfaPurposeFactory: MfaPurposeFactory
) {
    @GetMapping("/setup-2fa")
    fun setup2fa(@AuthenticationPrincipal user: User): ResponseEntity<ByteArray> {
        val (secret, otpUri) = totpService.generateSecret(user.email)
        userService.updateUserSecret(user.userId, secret)
        val qrCode = qrCodeService.generateQrCode(otpUri)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode)
    }

    @PostMapping("/verify-2fa")
    fun verify2fa(
        @RequestParam code: String,
        @AuthenticationPrincipal user: User,
        @RequestParam(HttpHeaders.AUTHORIZATION) token: String
    ): ResponseEntity<String> {
        val purpose = jwtService.getClaims(token.removePrefix("Bearer "))?.get(Claims.MFA_ACTION) as String?
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val mfaPurpose = MfaPurpose.get(purpose)
        val isValid = validationService.validateCode(user.totp!!, code)
        return if (isValid) {
            val mfaResult = mfaPurposeFactory.getMfaPurposeHandler(mfaPurpose)
                .handle(username = user.username, userId = user.userId)
            return ResponseEntity.ok().headers { it.add(HttpHeaders.AUTHORIZATION, mfaResult) }
                .body("MFA authentication successful")
        } else {
            ResponseEntity.badRequest().body("Invalid code")
        }
    }
}