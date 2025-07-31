package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

/**
 * Reactive repository interface for managing [Order] domain entities.
 *
 * Provides non-blocking, reactive operations for order management using Project Reactor's [Mono].
 * All methods return [Mono] publishers that complete asynchronously.
 *
 * Implementations should handle conversion between domain models and persistence entities.
 */
interface OrderRepository {

    /**
     * Persists a new order or updates an existing one.
     *
     * @param order The order domain object to save
     * @return [Mono] emitting the persisted order's ID upon successful completion
     * @throws org.springframework.dao.DataAccessException if persistence operation fails
     */
    fun save(order: Order): Mono<UUID>

    /**
     * Deletes an order by its unique identifier.
     *
     * @param orderId The ID of the order to delete
     * @return [Mono] emitting `true` if an order was deleted, `false` if no order was found
     * @throws org.springframework.dao.DataAccessException if deletion operation fails
     */
    fun delete(orderId: String): Mono<Boolean>

    /**
     * Retrieves an order's existence status by its ID.
     *
     * Note: This differs from conventional repository patterns by returning existence status
     * rather than the entity itself. Consider revising based on use case requirements.
     *
     * @param orderId The ID of the order to check
     * @return [Mono] emitting `true` if order exists, `false` otherwise
     * @throws org.springframework.dao.DataAccessException if retrieval operation fails
     */
    fun getById(orderId: String): Mono<Order>

    /**
     * Retrieves a complete order by its tracking ID.
     *
     * @param trackingId The unique tracking identifier
     * @return [Mono] emitting the [Order] if found, or empty Mono if not found
     * @throws org.springframework.dao.DataAccessException if retrieval operation fails
     */
    fun getByTrackingId(trackingId: String): Mono<Order>

    /**
     * Updates the status of an order identified by tracking ID.
     *
     * @param trackingId The tracking ID of the order to update
     * @param status The new status to apply
     * @return [Mono] emitting `true` if update was successful, `false` if no order was found
     * @throws org.springframework.dao.DataAccessException if update operation fails
     * @throws IllegalStateException if attempting an invalid status transition
     */
    fun updateOrderStatus(trackingId: String, status: OrderStatus): Mono<Boolean>

    fun updateOrder(
        orderStatus: OrderStatus? = null,
        subtotal: Double? = null,
        taxAmount: Double? = null,
        shippingAmount: Double? = null,
        discountAmount: Double? = null,
        currency: String? = null,
        billingAddress: Address? = null,
        shippingAddress: Address? = null,
        notes: String?? = null,
        confirmedAt: LocalDateTime?? = null,
        cancelledAt: LocalDateTime?? = null,
        paymentMethod: PaymentMethod? = null,
        paymentProvider: String?? = null,
        paymentStatus: PaymentStatus? = null,
        providerPaymentId: String?? = null,
        userId: String,
        orderId: String,
    ): Mono<Boolean>
}