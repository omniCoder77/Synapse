package com.ethyllium.authservice.domain.port.driven

interface OtpDeliveryService {
    fun sendOtp(otp: String, userId: String, phoneNumber: String)
}