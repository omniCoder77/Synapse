package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class MultipleRefundsResponse(
    @JsonProperty("items")
    val items: List<RefundItem>,
    
    @JsonProperty("count")
    val count: Int,
    
    @JsonProperty("entity")
    val entity: String = "collection"
)

data class RefundItem(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("payment_id")
    val paymentId: String,
    
    @JsonProperty("amount")
    val amount: Long,
    
    @JsonProperty("currency")
    val currency: String,
    
    @JsonProperty("notes")
    val notes: Map<String, String>?,
    
    @JsonProperty("receipt")
    val receipt: String?,
    
    @JsonProperty("status")
    val status: String,
    
    @JsonProperty("speed_processed")
    val speedProcessed: String,
    
    @JsonProperty("speed_requested")
    val speedRequested: String,
    
    @JsonProperty("created_at")
    val createdAt: Long,
    
    @JsonProperty("acquirer_data")
    val acquirerData: AcquirerData?
)

data class AcquirerData(
    @JsonProperty("arn")
    val arn: String?
)