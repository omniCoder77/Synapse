package com.synapse.paymentservice.domain.model

import org.springframework.http.HttpStatus

data class WebhookResult(
    val success: Boolean,
    val message: String,
    val statusCode: HttpStatus
)