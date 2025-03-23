package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.service.JwtService
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.stereotype.Component

@Component
class ResetPasswordPurposeHandler(private val jwtService: JwtService) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        val accessToken = jwtService.generateAccessToken(
            subject = username,
            additionalClaims = mapOf(username to Claims.USER_ID, MfaPurpose.RESET_PASSWORD.name to Claims.PURPOSE)
        )
        return accessToken
    }
}