package com.synapse.paymentservice.infrastructure.output.razorpay

import com.razorpay.RazorpayClient
import com.synapse.paymentservice.application.dto.request.OrderRequest
import com.synapse.paymentservice.application.dto.request.OrderStatus
import com.synapse.paymentservice.application.dto.response.OrderResponse
import com.synapse.paymentservice.domain.exception.OrderCreationException
import com.synapse.paymentservice.domain.port.incoming.OrderServicePort
import com.synapse.paymentservice.domain.port.outgoing.OrderRepositoryPort
import com.synapse.paymentservice.infrastructure.output.persistence.jpa.JpaOutboxEventEntityRepository
import com.synapse.paymentservice.infrastructure.output.persistence.jpa.OutboxEventEntity
import jakarta.transaction.Transactional
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class RazorpayPaymentGateway(
    @Value("\${razorpay.key.id}") private val razorpayKeyId: String,
    @Value("\${razorpay.secret.key}") private val razorpaySecretKey: String,
    private val jpaOutboxEventRepository: JpaOutboxEventEntityRepository,
    private val orderRepositoryPort: OrderRepositoryPort,
) : OrderServicePort {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val client = RazorpayClient(razorpayKeyId, razorpaySecretKey)

    override fun createOrder(productOrderRequest: OrderRequest): OrderResponse {
        val receiptId = TimeBasedIdGenerator.nextId()
        val order = JSONObject().apply {
            put("amount", productOrderRequest.amount * 100)
            put("currency", "INR")
            put("receipt", receiptId)
        }

        return try {
            val createdOrder = client.orders.create(order)
            logger.info("Created Razorpay order: {}", createdOrder)

            val localOrder = productOrderRequest.toOrder().apply {
                razorpayOrderId = createdOrder.get("id")
                receipt = receiptId
                status = OrderStatus.get(createdOrder.get("status"))
            }
            orderRepositoryPort.save(localOrder)

            OrderResponse(createdOrder.get("id"), localOrder.status)
        } catch (e: Exception) {
            throw OrderCreationException("Unable to create order at this time. Please try again: ${e.message}")
        }
    }


    @Transactional
    override fun markPaid(orderId: String) {
        val order = orderRepositoryPort.findById(orderId)!!.copy(status = OrderStatus.PAID)
        orderRepositoryPort.save(order)
        jpaOutboxEventRepository.save(
            OutboxEventEntity(
                orderId, order.razorpayOrderId
            )
        )
    }

}

object TimeBasedIdGenerator {
    private val startTime = System.currentTimeMillis()
    private val counter = AtomicLong(0)

    fun nextId(): String {
        val timestamp = System.currentTimeMillis() - startTime
        val count = counter.incrementAndGet()
        return timestamp.toString(36) + count.toString(36)
    }
}
