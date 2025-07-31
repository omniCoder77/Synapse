package com.synapse.orderservice.application.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.domain.port.driven.OrderItemRepository
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.domain.port.driven.OutboxRepository
import com.synapse.orderservice.domain.port.driver.OrderService
import com.synapse.orderservice.infrastructure.inbound.grpc.grpc.ProductValidationService
import com.synapse.orderservice.infrastructure.inbound.rest.rest.dto.IdQuantity
import com.synapse.orderservice.infrastructure.outbound.kafka.event.OrderCreationEvent
import com.synapse.orderservice.infrastructure.outbound.postgres.util.AggregateTypes
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Component
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productValidationService: ProductValidationService,
    private val orderItemRepository: OrderItemRepository,
    private val outboxRepository: OutboxRepository
) : OrderService {

    override fun getOrderById(orderId: String): Mono<Order> {
        return orderRepository.getById(orderId)
    }

    override fun updateOrder(
        userId: String,
        orderId: OrderId,
        status: OrderStatus?,
        items: List<OrderItem>,
        paymentMethod: PaymentMethod?,
        paymentStatus: PaymentStatus?
    ) {
        val update = Update.update("order_id", orderId)
        status?.let { update.set("status", it.name) }

    }

    @Transactional
    override fun createOrder(
        items: List<IdQuantity>,
        paymentMethod: PaymentMethod,
        billingAddress: Address,
        shippingAddress: Address,
        notes: String?,
        currency: String,
        userId: String
    ): Mono<UUID> {
        val order = Order(
            orderId = OrderId(UUID.randomUUID()),
            userId = UserId(UUID.fromString(userId)),
            orderStatus = OrderStatus.PENDING,
            paymentMethod = paymentMethod,
            billingAddress = billingAddress,
            shippingAddress = shippingAddress,
            notes = notes,
            currency = currency,
            subtotal = 0.0,
            paymentStatus = PaymentStatus.REQUESTED
        )
        val event = OrderCreationEvent(items, userId, order.orderId.value.toString(), currency, paymentMethod)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        return orderRepository.save(order).flatMap { savedOrder ->
            outboxRepository.save(
                aggregateId = savedOrder,
                aggregateType = AggregateTypes.ORDER_CREATION_REQUEST,
                eventType = "OrderCreationRequested",
                payload = kafkaEvent
            ).map { savedOrder }
        }
    }
}