package com.ethyllium.productservice.repository

import com.ethyllium.productservice.model.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OutboxRepository : JpaRepository<OutboxEvent, String> {
    fun findBySent(sent: Boolean): MutableList<OutboxEvent>

    @Modifying
    @Query("UPDATE OutboxEvent SET sent = true WHERE outboxEventId = :eventId")
    fun sentEvent(outboxEventId: String)
    fun existsOutboxEventByOutboxEventId(outboxEventId: String): Boolean
}