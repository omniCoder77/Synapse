package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.repository.LoginAttemptRepository
import com.ethyllium.authservice.service.JwtService
import com.ethyllium.authservice.util.Claims
import org.springframework.stereotype.Component

@Component
class NewDeviceLoginPurposeHandler(
    private val loginAttemptRepository: LoginAttemptRepository, private val jwtService: JwtService
) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        loginAttemptRepository.addDeviceFingerprint(username, data[0])
        loginAttemptRepository.resetAttempt(username)
        val accessToken = jwtService.generateAccessToken(username, additionalClaims = mapOf(userId to Claims.USER_ID))
        return accessToken
    }
}