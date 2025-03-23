package com.ethyllium.authservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Duration
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

@Service
class TokenService(
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${email.verification.token.expiration}") private val expirationMinutes: Long
) {

    companion object {
        const val TOKEN_PREFIX = "verification:"
    }

    fun generateToken(userId: String): String {
        val token = UUID.randomUUID().toString()
        val key = "$TOKEN_PREFIX$token"
        redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(expirationMinutes))
        return token
    }

    fun generateOtp(userId: String): String {
        val random = SecureRandom()
        val otp =
            IntStream.range(0, 8).map { random.nextInt(10) }.mapToObj { it.toString() }.collect(Collectors.joining())
        val key = "$TOKEN_PREFIX$otp"
        redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(expirationMinutes))
        return otp
    }

}