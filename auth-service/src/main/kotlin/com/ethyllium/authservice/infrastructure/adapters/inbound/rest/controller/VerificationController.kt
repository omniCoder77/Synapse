package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.controller

import com.ethyllium.authservice.application.service.ValidationService
import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.domain.port.driven.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/auth")
class VerificationController(
    private val validationService: ValidationService,
    private val tokenService: TokenService,
    private val loginAttemptRepository: LoginAttemptRepository
) {

    @GetMapping("/verify")
    fun verify(@RequestParam(value = "token", required = true) token: String): Mono<ResponseEntity<String>> {
        return validationService.verifyAccount(token).map {
            if (it) ResponseEntity.ok("OK")
            else ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/verify-email")
    fun verifyEmail(
        @RequestParam(value = "session", required = true) session: String
    ): Mono<ResponseEntity<String>> {
        return validationService.verifyLogin(session).flatMap { username ->
            if (username != null) {
                val accessToken = tokenService.generateAccessToken(username)
                loginAttemptRepository.resetAttempt(UUID.fromString(username)).then(
                    Mono.just(
                        ResponseEntity(
                            "OK", HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, accessToken) }, HttpStatus.OK
                        )
                    )
                )
            } else {
                Mono.just(ResponseEntity.notFound().build())
            }
        }
    }
}