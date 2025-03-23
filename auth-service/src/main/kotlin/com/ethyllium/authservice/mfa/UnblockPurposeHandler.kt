package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.service.JwtService
import org.springframework.stereotype.Component

@Component
class UnblockPurposeHandler(
    private val userRepository: UserRepository, private val jwtService: JwtService
) : MfaPurposeHandler {
    override fun handle(username: String, userId: String, vararg data: String): String {
        userRepository.unblockUser(userId)
        val accessToken = jwtService.generateAccessToken(username)
        return accessToken
    }
}