package com.ethyllium.productservice.infrastructure.adapter.outbound.communication

import com.ethyllium.productservice.domain.port.driven.OtpService
import com.twilio.rest.verify.v2.service.Verification
import com.twilio.rest.verify.v2.service.VerificationCheck
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TwilioOtpService(
    @Value("\${twilio.path-service-id}") private val pathServiceId: String
) : OtpService {
    override fun sendOtp(phoneNumber: String) {
        Verification.creator(pathServiceId, phoneNumber, "sms").create()
    }

    override fun verifyOtp(phoneNumber: String, otp: String): Boolean {
        return VerificationCheck.creator(pathServiceId).setTo(phoneNumber).setCode(otp).create().status == "approved"
    }
}