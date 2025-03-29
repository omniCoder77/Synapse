package com.ethyllium.searchservice.ports

import com.ethyllium.searchservice.model.OutboxEvent

interface EventProcessed {
    fun processedEvents(event: OutboxEvent)
}