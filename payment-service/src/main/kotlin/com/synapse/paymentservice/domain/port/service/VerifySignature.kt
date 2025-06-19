package com.synapse.paymentservice.domain.port.service

interface VerifySignature {
    fun verifyWebhookSignature(payload: String, razorpaySignature: String, secret: String): Boolean
}