package com.ethyllium.searchservice.repository

import com.ethyllium.searchservice.model.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OutboxRepository: JpaRepository<OutboxEvent, String> {
    fun existsOutboxEventByOutboxEventId(outboxEventId: String): Boolean
    fun findOutboxEventsBySent(sent: Boolean): MutableList<OutboxEvent>
}