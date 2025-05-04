package com.ethyllium.authservice.domain.port.driven

interface OtpGenerator {
    fun generateOtp(userId: String): String
}