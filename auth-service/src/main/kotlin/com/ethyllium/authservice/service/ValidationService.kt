package com.ethyllium.authservice.service

import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.service.TokenService.Companion.TOKEN_PREFIX
import com.twilio.Twilio
import com.warrenstrange.googleauth.GoogleAuthenticator
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class ValidationService(
    private val emailService: EmailService,
    private val tokenService: TokenService,
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${twilio.account.sid}") private val accountSid: String,
    @Value("\${twilio.auth.token}") private val authToken: String,
    @Value("\${twilio.phone.number}") private val twilioPhoneNumber: String,
) {

    fun sendVerificationMail(email: String, name: String, userId: String) {
        val token = tokenService.generateToken(userId)
        emailService.sendVerificationEmail(
            to = email, name = name, verificationToken = token
        )
    }

    @Transactional
    fun verifyAccount(token: String): Boolean {
        val userId = validateToken(token) ?: return false
        val user = userRepository.findById(userId).orElse(null) ?: return false
        userRepository.enableUser(user.userId)
        return true
    }

    fun sendVerificationOtp(phoneNumber: String, userId: String) {
        Twilio.init(accountSid, authToken)
        val token = tokenService.generateOtp(userId)
        Message.creator(PhoneNumber(phoneNumber),
                                PhoneNumber(twilioPhoneNumber), "Your verification code for Synapse is $token").create();
    }

    fun validateCode(secret: String, code: String): Boolean {
        return GoogleAuthenticator().authorize(secret, code.toInt())
    }

    fun validateToken(token: String): String? {
        val key = "$TOKEN_PREFIX$token"
        val userId = redisTemplate.opsForValue().get(key) ?: return null

        redisTemplate.delete(key)

        return userId as String
    }
}