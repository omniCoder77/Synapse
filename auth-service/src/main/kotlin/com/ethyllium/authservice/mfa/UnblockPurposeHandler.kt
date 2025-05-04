package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import com.ethyllium.authservice.domain.port.driven.TokenService
import org.springframework.stereotype.Component

@Component
class UnblockPurposeHandler(
    private val jpaUserEntityRepository: JpaUserEntityRepository,
    private val tokenService: TokenService
) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        jpaUserEntityRepository.unblockUser(userId)
        val accessToken = tokenService.generateAccessToken(username)
        return accessToken
    }
}