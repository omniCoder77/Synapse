package com.ethyllium.authservice.service

import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.service.TokenService.Companion.TOKEN_PREFIX
import com.warrenstrange.googleauth.GoogleAuthenticator
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class ValidationService(
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    @Transactional
    fun verifyAccount(token: String): Boolean {
        val userId = validateToken(token) ?: return false
        val user = userRepository.findById(userId).orElse(null) ?: return false
        userRepository.enableUser(user.username)
        return true
    }

    fun validateCode(secret: String, code: String): Boolean {
        return GoogleAuthenticator().authorize(secret, code.toInt())
    }

    private fun validateToken(token: String): String? {
        val key = "$TOKEN_PREFIX$token"
        val userId = redisTemplate.opsForValue().get(key) ?: return null

        redisTemplate.delete(key)

        return userId as String
    }
}