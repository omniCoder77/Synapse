package com.ethyllium.authservice.infrastructure.adapters.communication

import com.ethyllium.authservice.domain.port.driven.OtpGenerator
import com.ethyllium.authservice.domain.util.Constants.Companion.EMAIL_TOKEN_PREFIX
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Duration
import java.util.stream.Collectors
import java.util.stream.IntStream

@Component
class OtpGeneratorAdapter(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${email.verification.token.expiration}") private val expirationMinutes: Long
) : OtpGenerator {
    override fun generateOtp(userId: String): String {
        val random = SecureRandom()
        val otp =
            IntStream.range(0, 8).map { random.nextInt(10) }.mapToObj { it.toString() }.collect(Collectors.joining())
        val key = "$EMAIL_TOKEN_PREFIX$otp"
        redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(expirationMinutes))
        return otp
    }
}