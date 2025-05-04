package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.service.LoginService
import com.ethyllium.authservice.application.dto.request.LoginRequest
import com.ethyllium.authservice.application.service.LoginAttempt
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class LoginController(private val loginService: LoginService) {

    @PostMapping("/login")
    fun login(
        @RequestHeader(HttpHeaders.AUTHORIZATION) password: String, @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<String> {
        val loginRes = loginService.login(
            email = loginRequest.email, password = password, deviceFingerprint = loginRequest.deviceFingerprint
        )
        return when (loginRes) {
            LoginAttempt.CredentialVerification -> ResponseEntity(
                "Verification link has been sent to your email address", HttpStatus.OK
            )

            LoginAttempt.InvalidCredentials -> ResponseEntity("Invalid email/password", HttpStatus.BAD_REQUEST)
            is LoginAttempt.MFALogin -> return ResponseEntity(
                "Enter the verification code from the authenticator app",
                HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, loginRes.token) },
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