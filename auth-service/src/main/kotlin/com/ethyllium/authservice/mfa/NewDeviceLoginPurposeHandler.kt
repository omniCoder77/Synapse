package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaLoginAttemptRepository
import com.ethyllium.authservice.util.Claims
import org.springframework.stereotype.Component

@Component
class NewDeviceLoginPurposeHandler(
    private val jpaLoginAttemptRepository: JpaLoginAttemptRepository, private val tokenService: TokenService
) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        jpaLoginAttemptRepository.addDeviceFingerprint(username, data[0])
        jpaLoginAttemptRepository.resetAttempt(username)
        val accessToken = tokenService.generateAccessToken(username, additionalClaims = mapOf(userId to Claims.USER_ID))
        return accessToken
    }
}