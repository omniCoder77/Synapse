package com.synapse.paymentservice.application.service

import com.synapse.paymentservice.domain.event.DomainEvent
import com.synapse.paymentservice.domain.model.WebhookResult
import com.synapse.paymentservice.domain.port.incoming.WebhookHandler
import com.synapse.paymentservice.domain.port.outgoing.EventPublisher
import com.synapse.paymentservice.domain.port.service.VerifySignature
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class PaymentWebhookService(
    private val signatureVerifier: VerifySignature,
    private val eventPublisher: EventPublisher,
    @Value("\${webhook.secret}") private val webhookSecret: String,
) : WebhookHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleWebhook(payload: String, signature: String): WebhookResult {
        return try {
            if (!signatureVerifier.verifyWebhookSignature(payload, signature, secret = webhookSecret)) {
                logger.warn("Invalid webhook signature")
                return WebhookResult(false, "Invalid signature", HttpStatus.BAD_REQUEST)
            }

            val event = parseEvent(payload)
            eventPublisher.publishEvent(event)

            WebhookResult(true, "Webhook processed", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Webhook processing failed", e)
            WebhookResult(false, "Processing failed", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun parseEvent(payload: String): DomainEvent {
        val payloadJson = JSONObject(payload)
        val eventType = payloadJson.getString("event")
        val payment = payloadJson.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity")

        return when (eventType) {
            "payment.authorized" -> DomainEvent.PaymentAuthorizedEvent(
                razorpayOrderId = payment.getString("order_id"), paymentId = payment.getString("id"), status = "AUTHORIZED"
            )

            "payment.failed" -> DomainEvent.PaymentFailedEvent(
                razorpayOrderId = payment.getString("order_id"), paymentId = payment.getString("id"), status = "FAILED"
            )

            else -> throw IllegalArgumentException("Invalid event type: $eventType")
        }
    }
}