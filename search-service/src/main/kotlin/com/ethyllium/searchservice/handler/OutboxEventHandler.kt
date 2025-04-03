package com.ethyllium.searchservice.handler

import com.ethyllium.searchservice.model.OutboxEvent
import com.ethyllium.searchservice.repository.OutboxRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class OutboxEventHandler(private val outboxRepository: OutboxRepository) {

    fun toOutboxEvent(outboxEvent: String): OutboxEvent {
        return jacksonObjectMapper().readValue(outboxEvent, OutboxEvent::class.java)
    }

    fun isDuplicate(outboxEventId: String): Boolean {
        return outboxRepository.existsOutboxEventByOutboxEventId(outboxEventId)
    }
}