package com.synapse.paymentservice.domain.port.incoming

import com.synapse.paymentservice.domain.model.WebhookResult

interface WebhookHandler {
    fun handleWebhook(payload: String, signature: String): WebhookResult
}