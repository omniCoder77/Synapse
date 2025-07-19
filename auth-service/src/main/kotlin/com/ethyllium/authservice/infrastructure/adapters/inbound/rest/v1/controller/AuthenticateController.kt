package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.application.util.Claims
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.AuthenticateResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticateController(private val tokenService: TokenService, private val userRepository: UserRepository) {

    @GetMapping("/authenticate")
    fun authenticate(@RequestHeader(HttpHeaders.AUTHORIZATION) bearerToken: String): ResponseEntity<AuthenticateResponse> {
        val token = bearerToken.removePrefix("Bearer ")
        val claims = tokenService.getClaims(token) ?: return ResponseEntity.notFound().build()
        val userId = claims.subject!!
        val roles = claims.get(Claims.ROLE, String::class.java)
        return ResponseEntity.ok(AuthenticateResponse(userId, roles))
    }
}