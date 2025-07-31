package com.synapse.orderservice.infrastructure.outbound.postgres

import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.infrastructure.outbound.postgres.entity.OrderEntity
import com.synapse.orderservice.infrastructure.outbound.postgres.entity.toEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

/**
 * Reactive implementation of [OrderRepository] using R2DBC for database operations.
 *
 * This component provides reactive CRUD operations for [Order] entities, converting between
 * domain models and database entities as needed.
 *
 * @constructor Creates a new OrderRepositoryImpl with the given R2DBC template
 * @param r2dbcEntityTemplate The reactive entity template for R2DBC operations
 */
@Component
class OrderRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) : OrderRepository {

    /**
     * Saves an [Order] to the database.
     *
     * @param order The order domain object to persist
     * @return [Mono] emitting the orderId of the persisted order
     * @throws org.springframework.dao.DataAccessException if there's an error during persistence
     */
    override fun save(order: Order): Mono<UUID> {
        return r2dbcEntityTemplate.insert(order.toEntity()).map { it.orderId }
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderId The ID of the order to delete
     * @return [Mono] emitting true if an order was deleted, false otherwise
     * @throws org.springframework.dao.DataAccessException if there's an error during deletion
     */
    override fun delete(orderId: String): Mono<Boolean> {
        return r2dbcEntityTemplate.delete(
            Query.query(Criteria.where("orderId").`is`(orderId)), OrderEntity::class.java
        ).map { it > 0 }
    }

    /**
     * Retrieves an order by its ID
     *
     * @param orderId The ID of the order to retrieve
     * @return [Mono] emitting true if an order was found
     * @throws org.springframework.dao.DataAccessException if there's an error during operation
     */
    override fun getById(orderId: String): Mono<Order> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where("orderId").`is`(orderId)), OrderEntity::class.java
        ).map { it.toDomain() }
    }

    /**
     * Retrieves an order by its tracking ID.
     *
     * @param trackingId The tracking ID of the order to find
     * @return [Mono] emitting the found [Order], or empty if not found
     * @throws org.springframework.dao.DataAccessException if there's an error during retrieval
     */
    override fun getByTrackingId(trackingId: String): Mono<Order> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where("tracking_id").`is`(trackingId)), OrderEntity::class.java
        ).map { it.toDomain() }
    }

    /**
     * Updates the status of an order identified by its tracking ID.
     *
     * @param trackingId The tracking ID of the order to update
     * @param status The new status to set
     * @return [Mono] emitting true if the order was updated, false otherwise
     * @throws org.springframework.dao.DataAccessException if there's an error during update
     */
    override fun updateOrderStatus(trackingId: String, status: OrderStatus): Mono<Boolean> {
        val update = Update.update("status", status)
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("tracking_id").`is`(trackingId)), update, OrderEntity::class.java
        ).map { it > 0 }
    }

    override fun updateOrder(
        orderStatus: OrderStatus?,
        subtotal: Double?,
        taxAmount: Double?,
        shippingAmount: Double?,
        discountAmount: Double?,
        currency: String?,
        billingAddress: Address?,
        shippingAddress: Address?,
        notes: String?,
        confirmedAt: LocalDateTime?,
        cancelledAt: LocalDateTime?,
        paymentMethod: PaymentMethod?,
        paymentProvider: String?,
        paymentStatus: PaymentStatus?,
        providerPaymentId: String?,
        userId: String,
        orderId: String
    ): Mono<Boolean> {
        val update = Update.update("user_id", userId).apply {
            orderStatus?.let { set("order_status", it.name) }
            subtotal?.let { set("subtotal", it) }
            taxAmount?.let { set("tax_amount", it) }
            shippingAmount?.let { set("shipping_amount", it) }
            discountAmount?.let { set("discount_amount", it) }
            currency?.let { set("currency", it) }
            billingAddress?.let { set("billing_address", it) }
            shippingAddress?.let { set("shipping_address", it) }
            notes?.let { set("notes", it) }
            confirmedAt?.let { set("confirmed_at", it) }
            cancelledAt?.let { set("cancelled_at", it) }
            paymentMethod?.let { set("payment_method", it.name) }
            paymentProvider?.let { set("payment_provider", it) }
            paymentStatus?.let { set("payment_status", it.name) }
            providerPaymentId?.let { set("provider_payment_id", it) }
        }
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("order_id").`is`(orderId).and(Criteria.where("user_id").`is`(userId))),
            update,
            OrderEntity::class.java
        ).map { it > 0 }
    }
}