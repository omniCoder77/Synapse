package com.ethyllium.authservice.api

import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.service.JwtService
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/auth"])
class ForgotPasswordController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @GetMapping("/forgot-password")
    fun sendVerificationToken(@RequestParam email: String): ResponseEntity<String> {
        val user = userRepository.findByEmail(email).firstOrNull() ?: return ResponseEntity.notFound().build()
        val accessToken = jwtService.generateAccessToken(
            user.username,
            additionalClaims = mapOf(Claims.PURPOSE to MfaPurpose.RESET_PASSWORD.name)
        )
        return ResponseEntity(HttpHeaders().apply { add(HttpHeaders.AUTHORIZATION, accessToken) }, HttpStatus.OK)
    }

    @GetMapping("/reset-password")
    fun verifyEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String, @RequestParam password: String): ResponseEntity<String> {
        val claims = jwtService.getClaims(token) ?: return ResponseEntity.notFound().build()
        if (claims[Claims.ACTION] == null) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity(HttpStatus.OK)
    }
}