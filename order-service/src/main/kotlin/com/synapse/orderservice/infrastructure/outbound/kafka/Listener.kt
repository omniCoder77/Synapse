package com.synapse.orderservice.infrastructure.outbound.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.orderservice.domain.model.OrderStatus
import com.synapse.orderservice.domain.port.driven.OrderItemRepository
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.domain.port.driven.OutboxRepository
import com.synapse.orderservice.infrastructure.inbound.grpc.grpc.ProductValidationService
import com.synapse.orderservice.infrastructure.outbound.kafka.event.OrderCreatedEvent
import com.synapse.orderservice.infrastructure.outbound.kafka.event.OrderCreationEvent
import com.synapse.orderservice.infrastructure.outbound.postgres.util.AggregateTypes
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import reactor.core.publisher.Mono
import java.util.*

@Component
class Listener(
    private val productValidationService: ProductValidationService,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val outboxRepository: OutboxRepository,
    @Qualifier("reactiveTransactionManager") private val transactionManager: ReactiveTransactionManager
) {

    val objectMapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    @KafkaListener(topics = [AggregateTypes.ORDER_CREATION_REQUEST], groupId = "order-service-group")
    fun listen(message: String) {
        val orderEvent = objectMapper.readValue(message, OrderCreationEvent::class.java)

        val transactionDefinition = DefaultTransactionDefinition().apply {
            propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
        }

        transactionManager.getReactiveTransaction(transactionDefinition).flatMap { transactionStatus ->
                Mono.zip(
                    productValidationService.createProduct(orderEvent.orderItems),
                    orderRepository.getById(orderEvent.orderId)
                ).flatMap { tuple ->
                    val event = OrderCreatedEvent(
                        orderId = orderEvent.orderId,
                        userId = orderEvent.userId,
                        amount = tuple.t1.second,
                        currency = orderEvent.currency,
                        paymentMethod = orderEvent.paymentMethod
                    )
                    val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)

                    orderRepository.updateOrder(
                        orderId = orderEvent.orderId,
                        userId = orderEvent.userId,
                        orderStatus = OrderStatus.PENDING,
                        subtotal = tuple.t1.second
                    ).then(
                        orderItemRepository.save(tuple.t1.first)
                    ).then(
                        outboxRepository.save(
                            aggregateId = UUID.fromString(orderEvent.orderId),
                            aggregateType = AggregateTypes.ORDER_CREATED,
                            eventType = "OrderCreatedEvent",
                            payload = kafkaEvent
                        )
                    ).then(
                        transactionManager.commit(transactionStatus)
                    ).onErrorResume { error ->
                        transactionManager.rollback(transactionStatus).then(Mono.error(error))
                    }
                }
            }.subscribe()
    }
}