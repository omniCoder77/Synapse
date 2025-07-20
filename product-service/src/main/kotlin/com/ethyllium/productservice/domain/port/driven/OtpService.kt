package com.ethyllium.productservice.domain.port.driven

interface OtpService {
    fun sendOtp(phoneNumber: String)
    fun verifyOtp(phoneNumber: String, otp: String): Boolean
}