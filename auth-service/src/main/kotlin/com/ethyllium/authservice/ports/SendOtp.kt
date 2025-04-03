package com.ethyllium.authservice.ports

interface SendOtp {
    fun sendOtp(otp: String, userId: String, phoneNumber: String)
}