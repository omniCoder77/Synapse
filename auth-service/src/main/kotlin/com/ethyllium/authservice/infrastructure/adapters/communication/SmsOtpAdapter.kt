package com.ethyllium.authservice.infrastructure.adapters.communication

import com.ethyllium.authservice.domain.port.driven.OtpDeliveryService
import com.ethyllium.authservice.domain.port.driven.OtpGenerator
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SmsOtpAdapter(
    @Value("\${twilio.account.sid}") private val accountSid: String,
    @Value("\${twilio.auth.token}") private val authToken: String,
    @Value("\${twilio.phone.number}") private val twilioPhoneNumber: String,
    private val otpGenerator: OtpGenerator,
): OtpDeliveryService {
    override fun sendOtp(otp: String, userId: String, phoneNumber: String) {
        Twilio.init(accountSid, authToken)
        val token = otpGenerator.generateOtp(userId)
        Message.creator(
            PhoneNumber(phoneNumber), PhoneNumber(twilioPhoneNumber), "Your verification code for Synapse is $token"
        ).create()
    }
}