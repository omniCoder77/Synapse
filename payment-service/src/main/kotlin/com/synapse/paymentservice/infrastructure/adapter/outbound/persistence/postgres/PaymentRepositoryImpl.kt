package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres

import com.razorpay.Utils
import com.synapse.paymentservice.domain.exception.OrderCreationException
import com.synapse.paymentservice.domain.model.OrderResponse
import com.synapse.paymentservice.domain.model.Payment
import com.synapse.paymentservice.domain.model.PaymentRequest
import com.synapse.paymentservice.domain.model.PaymentStatus
import com.synapse.paymentservice.domain.port.driven.PaymentRepository
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.PaymentEntity
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.toPaymentEntity
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.RazorpayOrderOperations
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class PaymentRepositoryImpl(
    @Value("\${razorpay.webhook.secret}") private val webhookSecret: String,
) : PaymentRepository {

    @Autowired
    private lateinit var razorpayOrderOperations: RazorpayOrderOperations

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun createPaymentOrder(paymentRequest: PaymentRequest, userId: String): Mono<OrderResponse> {
        return Mono.fromCallable {
            try {
                val createdOrder = razorpayOrderOperations.createOrderPayment(
                    paymentRequest.amount, "INR", paymentRequest.productOrderId.toString()
                )
                logger.info("Created Razorpay order: {}", createdOrder)
                if (createdOrder.isFailure) {
                    throw OrderCreationException("Failed to create order: ${createdOrder.exceptionOrNull()?.message}")
                }
                val order = createdOrder.getOrNull()!!
                OrderResponse(
                    orderId = order.id,
                    paymentStatus = PaymentStatus.get(order.status),
                    amount = order.amount,
                    notes = order.notes,
                    receipt = order.receipt,
                    userId = userId
                )
            } catch (e: Exception) {
                logger.error("Failed to create razorpay order", e)
                throw OrderCreationException("Unable to create order at this time. Please try again: ${e.message}")
            }
        }
    }

    override fun save(payment: Payment, productOrderId: UUID): Mono<Payment> {
        val entity = payment.toPaymentEntity(productOrderId)
        return r2dbcEntityTemplate.insert(entity).map { it.toPayment() }
    }

    override fun findById(id: String): Mono<Payment> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where("order_id").`is`(id)), PaymentEntity::class.java
        ).map { it.toPayment() }
    }

    override fun findByRazorpayOrderId(razorpayOrderId: String): Mono<Payment> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where("razorpay_order_id").`is`(razorpayOrderId)), PaymentEntity::class.java
        ).map { it.toPayment() }
    }

    override fun paid(razorpayOrderId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("razorpay_order_id").`is`(razorpayOrderId))
        val update = Update.update("status", PaymentStatus.PAID.name)
        return r2dbcEntityTemplate.update(query, update, PaymentEntity::class.java).map { it > 0 }
    }

    override fun verification(
        payload: String, signature: String
    ): Boolean {
        return Utils.verifyWebhookSignature(payload, signature, webhookSecret)
    }

    override fun getByProductOrderId(productOrderId: String, userId: String): Mono<PaymentEntity> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(
                Criteria.where("product_order_id").`is`(UUID.fromString(productOrderId)).and(
                    Criteria.where("user_id").`is`(userId)
                )
            ), PaymentEntity::class.java
        )
    }

    override fun failed(orderId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("razorpay_order_id").`is`(orderId))
        val update = Update.update("status", PaymentStatus.FAILED.name)
        return r2dbcEntityTemplate.update(query, update, PaymentEntity::class.java).map { it > 0 }
    }

    override fun refund(refundId: String, paymentId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("payment_id").`is`(paymentId))
        val update = Update.update("refund_id", refundId)
        return r2dbcEntityTemplate.update(query, update, PaymentEntity::class.java).map { it > 0 }
    }
}