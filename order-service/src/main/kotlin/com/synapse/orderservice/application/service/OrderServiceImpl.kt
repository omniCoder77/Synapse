package com.synapse.orderservice.application.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.orderservice.application.event.OrderCreatedEvent
import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.domain.port.driven.OrderItemRepository
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.domain.port.driven.OutboxRepository
import com.synapse.orderservice.domain.port.driver.OrderService
import com.synapse.orderservice.infrastructure.inbound.web.grpc.ProductValidationService
import com.synapse.orderservice.infrastructure.inbound.web.rest.dto.IdQuantity
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
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

    override fun createOrder(
        items: List<IdQuantity>,
        paymentMethod: PaymentMethod,
        billingAddress: Address,
        shippingAddress: Address,
        notes: String?,
        currency: String,
        userId: String
    ): Mono<Void> {
        return productValidationService.createProduct(items).flatMap { (orderItems, amount) ->
            val order = Order(
                orderId = OrderId(UUID.randomUUID()),
                userId = UserId(UUID.fromString(userId)),
                orderStatus = OrderStatus.PENDING,
                paymentMethod = paymentMethod,
                billingAddress = billingAddress,
                shippingAddress = shippingAddress,
                notes = notes,
                currency = currency,
                subtotal = amount,
                paymentStatus = PaymentStatus.PENDING
            )
            val total = order.subtotal + order.taxAmount + order.shippingAmount - order.discountAmount
            val event = OrderCreatedEvent(total, order.currency)
            val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
            orderRepository.save(order).subscribeOn(Schedulers.boundedElastic()).subscribe()
            orderItemRepository.save(orderItems).subscribeOn(Schedulers.boundedElastic()).subscribe()
            outboxRepository.save(
                aggregateId = order.orderId.value,
                aggregateType = "Order",
                eventType = "OrderCreatedEvent",
                payload = kafkaEvent
            ).subscribeOn(Schedulers.boundedElastic()).then()
        }
    }
}