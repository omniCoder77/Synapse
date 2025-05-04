package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.port.driven.CodeValidator
import com.ethyllium.authservice.domain.util.Constants.Companion.TOKEN_PREFIX
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class ValidationService(
    private val jpaUserEntityRepository: JpaUserEntityRepository,
    private val redisTemplate: StringRedisTemplate,
    private val codeValidator: CodeValidator,
) {

    @Transactional
    fun verifyAccount(token: String): Boolean {
        val userId = validateToken(token) ?: return false
        val user = jpaUserEntityRepository.findById(userId).orElse(null) ?: return false
        jpaUserEntityRepository.enableUser(user.username)
        return true
    }

    fun validateCode(secret: String, code: String): Boolean {
        return codeValidator.validateCode(secret, code)
    }

    private fun validateToken(token: String): String? {
        val key = "$TOKEN_PREFIX$token"
        val userId = redisTemplate.opsForValue().get(key) ?: return null
        redisTemplate.delete(key)
        return userId as String
    }
}