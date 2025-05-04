package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.stereotype.Component

@Component
class ResetPasswordPurposeHandler(private val tokenService: TokenService) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        val accessToken = tokenService.generateAccessToken(
            subject = username,
            additionalClaims = mapOf(username to Claims.USER_ID, MfaPurpose.RESET_PASSWORD.name to Claims.PURPOSE)
        )
        return accessToken
    }
}