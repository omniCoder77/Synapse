package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.lang.String

@Repository
interface JpaOutboxEventEntityRepository : JpaRepository<OutboxEventEntity, String>