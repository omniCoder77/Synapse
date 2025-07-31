package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres

import com.synapse.paymentservice.domain.model.Refund
import com.synapse.paymentservice.domain.port.driven.RefundRepository
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.RefundEntity
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.toEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class RefundRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) : RefundRepository {
    override fun refundProcessed(refundId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("refund_id").`is`(refundId))
        val update = Update.update("status", "PROCESSED")
        return r2dbcEntityTemplate.update(query, update, RefundEntity::class.java).map { it > 0 }
    }

    override fun refundFailed(refundId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("refund_id").`is`(refundId))
        val update = Update.update("status", "FAILED")
        return r2dbcEntityTemplate.update(query, update, RefundEntity::class.java).map { it > 0 }
    }

    override fun refundCreated(orderId: String) {
    }

    override fun save(refund: Refund): Mono<Boolean> {
        return r2dbcEntityTemplate.insert(refund.toEntity()).map { true }.onErrorResume { Mono.just(false) }
    }
}