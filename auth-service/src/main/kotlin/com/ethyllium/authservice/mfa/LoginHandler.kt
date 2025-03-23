package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.service.JwtService
import com.ethyllium.authservice.util.Claims
import org.springframework.stereotype.Component

@Component
class LoginHandler(
    private val jwtService: JwtService
) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String? {
        val accessToken = jwtService.generateAccessToken(subject = username, mapOf(userId to Claims.USER_ID))
        return accessToken
    }
}