package com.ethyllium.authservice.ports

interface SendMail {
    fun sendVerificationEmail(to: String, verificationToken: String)
}