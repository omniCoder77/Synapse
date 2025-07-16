package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.LoginRequest
import com.ethyllium.authservice.domain.model.LoginAttempt
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driver.LoginUseCase
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class LoginController(
    private val tokenService: TokenService,
    private val loginUseCase: LoginUseCase
) {

    @PostMapping("/login")
    fun login(
        @RequestHeader(HttpHeaders.AUTHORIZATION, required = false) token: String? = null,
        @RequestBody loginRequest: LoginRequest,
    ): Mono<ResponseEntity<String>> {
        val isMfaLogin = if (token != null) {
            (tokenService.getClaims(token)?.get(MFAController.MFA_VERIFIED) as Boolean?) ?: false
        } else false
        return loginUseCase.login(
            email = loginRequest.email,
            password = loginRequest.password,
            deviceFingerprint = loginRequest.deviceFingerprint,
            isMfaLogin = isMfaLogin
        ).map { loginRes ->
            when (loginRes) {
                LoginAttempt.CredentialVerification -> ResponseEntity(
                    "Verification link has been sent to your email address", HttpStatus.OK
                )

                LoginAttempt.InvalidCredentials -> ResponseEntity("Invalid email/password", HttpStatus.BAD_REQUEST)
                is LoginAttempt.MFALogin -> ResponseEntity(
                    "Enter the verification code from the authenticator app", HttpStatus.OK
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
}