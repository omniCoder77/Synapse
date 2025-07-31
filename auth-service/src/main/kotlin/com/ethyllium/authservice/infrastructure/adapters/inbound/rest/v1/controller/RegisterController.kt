package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.domain.model.RegisterResult
import com.ethyllium.authservice.domain.port.driver.RegisterUserUseCase
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.ApiResponse
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.AuthResponse
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.RegisterRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class RegisterController(
    private val registerUserUseCase: RegisterUserUseCase
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): Mono<ResponseEntity<ApiResponse>> {
        return registerUserUseCase.register(registerRequest).map { token ->
            when (token) {
                is RegisterResult.Failure -> {
                    logger.info(token.error)
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.Error(token.error))
                }

                is RegisterResult.MfaImage -> {
                    logger.info("QR Code generated for MFA setup for ${registerRequest.email}")
                    ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(ApiResponse.Success(token.mfaQrCode))
                }

                is RegisterResult.Token -> {
                    logger.info("Registration Successful for ${registerRequest.email}")
                    ResponseEntity.ok().headers { it.add(HttpHeaders.AUTHORIZATION, token.accessToken) }.body(
                        ApiResponse.Success(
                            AuthResponse(
                                accessToken = token.accessToken,
                                refreshToken = token.refreshToken,
                                tokenType = "bearer",
                                expiresIn = 5 * 60 * 1000
                            )
                        )
                    )
                }
            }
        }
    }
}