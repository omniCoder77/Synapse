package com.synapse.paymentservice.domain.event

import java.time.Instant

open class DomainEvent(
    open val paymentId: String, val timestamp: Instant = Instant.now(), val payload: String = ""
)