package com.ethyllium.authservice.ports

interface OtpGenerator {
    fun generateOtp(userId: String): String
}
