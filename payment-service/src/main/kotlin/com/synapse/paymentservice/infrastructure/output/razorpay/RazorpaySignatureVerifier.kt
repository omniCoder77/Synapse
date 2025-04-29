package com.synapse.paymentservice.infrastructure.output.razorpay

import com.synapse.paymentservice.domain.port.service.VerifySignature
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class RazorpaySignatureVerifier : VerifySignature {

    override fun verifyWebhookSignature(
        payload: String, razorpaySignature: String, secret: String
    ): Boolean {
        val hmacSha256 = "HmacSHA256"
        return try {
            val mac = Mac.getInstance(hmacSha256)
            val secretKeySpec = SecretKeySpec(secret.toByteArray(), hmacSha256)
            mac.init(secretKeySpec)
            val digest = mac.doFinal(payload.toByteArray())
            val generatedSignature = Base64.getEncoder().encodeToString(digest)
            generatedSignature == razorpaySignature
        } catch (_: Exception) {
            false
        }
    }
}