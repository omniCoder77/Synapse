package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.util.Claims
import org.springframework.stereotype.Component

@Component
class LoginHandler(private val tokenService: TokenService) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        val accessToken = tokenService.generateAccessToken(subject = username, mapOf(userId to Claims.USER_ID))
        return accessToken
    }
}