package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class FetchMultipleRefundsRequest(
    @JsonProperty("count")
    val count: Int? = 10,
    
    @JsonProperty("skip")
    val skip: Int? = 0,
    
    @JsonProperty("status")
    val status: String? = null,
    
    @JsonProperty("receipt")
    val receipt: String? = null
)