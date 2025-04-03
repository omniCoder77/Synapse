package com.ethyllium.authservice.service

import com.ethyllium.authservice.ports.OtpGenerator
import com.ethyllium.authservice.ports.SendOtp
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OtpService(
    @Value("\${twilio.account.sid}") private val accountSid: String,
    @Value("\${twilio.auth.token}") private val authToken: String,
    @Value("\${twilio.phone.number}") private val twilioPhoneNumber: String,
    private val otpGenerator: OtpGenerator
) : SendOtp {
    override fun sendOtp(otp: String, userId: String, phoneNumber: String) {
        Twilio.init(accountSid, authToken)
        val token = otpGenerator.generateOtp(userId)
        Message.creator(
            PhoneNumber(phoneNumber), PhoneNumber(twilioPhoneNumber), "Your verification code for Synapse is $token"
        ).create()
    }
}