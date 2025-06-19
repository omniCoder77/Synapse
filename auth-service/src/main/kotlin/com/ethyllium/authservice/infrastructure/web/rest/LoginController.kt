package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.dto.request.LoginRequest
import com.ethyllium.authservice.application.service.LoginAttempt
import com.ethyllium.authservice.application.service.LoginService
import com.ethyllium.authservice.domain.port.driven.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class LoginController(private val loginService: LoginService, private val tokenService: TokenService) {

    @PostMapping("/login")
    fun login(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) token: String? = null,
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<String> {
        val isMfaLogin = if (token != null) {
            (tokenService.getClaims(token)?.get(MFAController.MFA_VERIFIED) as Boolean?) ?: false
        } else false
        val loginRes = loginService.login(
            email = loginRequest.email,
            password = loginRequest.password,
            deviceFingerprint = loginRequest.deviceFingerprint,
            isMfaLogin = isMfaLogin
        )
        return when (loginRes) {
            LoginAttempt.CredentialVerification -> ResponseEntity(
                "Verification link has been sent to your email address", HttpStatus.OK
            )

            LoginAttempt.InvalidCredentials -> ResponseEntity("Invalid email/password", HttpStatus.BAD_REQUEST)
            is LoginAttempt.MFALogin -> return ResponseEntity(
                "Enter the verification code from the authenticator app",
                HttpStatus.OK
            )

            is LoginAttempt.NewDeviceLogin -> ResponseEntity(
                "New device detected, enter the verification code from the authenticator app",
                HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, loginRes.token) },
                HttpStatus.OK
            )

            is LoginAttempt.Success -> ResponseEntity(
                "Successfully logged in",
                HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, loginRes.token) },
                HttpStatus.OK
            )
        }
    }
}