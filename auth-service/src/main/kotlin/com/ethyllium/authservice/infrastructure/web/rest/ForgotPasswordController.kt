package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/auth"])
class ForgotPasswordController(
    private val jpaUserEntityRepository: JpaUserEntityRepository,
    private val tokenService: TokenService,
) {

    @GetMapping("/forgot-password")
    fun sendVerificationToken(@RequestParam email: String): ResponseEntity<String> {
        val user = jpaUserEntityRepository.findByEmail(email).firstOrNull() ?: return ResponseEntity.notFound().build()
        val accessToken = tokenService.generateAccessToken(
            user.username, additionalClaims = mapOf(Claims.PURPOSE to MfaPurpose.RESET_PASSWORD.name)
        )
        return ResponseEntity(HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, accessToken) }, HttpStatus.OK)
    }

    @GetMapping("/reset-password")
    fun verifyEmail(
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        val claims = tokenService.getClaims(token) ?: return ResponseEntity.notFound().build()
        if (claims[Claims.ACTION] == null) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity(HttpStatus.OK)
    }
}