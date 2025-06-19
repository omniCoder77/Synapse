package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.dto.request.RegisterRequest
import com.ethyllium.authservice.application.dto.response.ApiResponse
import com.ethyllium.authservice.application.dto.response.AuthResponse
import com.ethyllium.authservice.application.service.RegisterResult
import com.ethyllium.authservice.application.service.RegisterUseCase
import com.ethyllium.authservice.domain.exception.EmailExistsException
import org.postgresql.util.PSQLException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class RegisterController(
    private val registerUseCase: RegisterUseCase
) {
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse> {
        try {
            val token = registerUseCase.register(registerRequest)
            return when (token) {
                is RegisterResult.Failure -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.Error(token.error))

                is RegisterResult.MfaImage -> ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                    .body(ApiResponse.Success(token.mfaQrCode))

                is RegisterResult.Token -> ResponseEntity.status(HttpStatus.OK)
                    .headers { it.add(HttpHeaders.AUTHORIZATION, token.accessToken) }.body(
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
        } catch (e: PSQLException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.Error(e.message!!))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.Error(e.message!!))
        } catch (e: EmailExistsException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.Error(e.message!!))
        }
    }
}