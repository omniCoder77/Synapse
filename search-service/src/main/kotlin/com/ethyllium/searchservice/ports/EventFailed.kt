package com.ethyllium.searchservice.ports

import com.ethyllium.searchservice.model.OutboxEvent

interface EventFailed {
    fun eventFailed(event: OutboxEvent)
}
