package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaOrderEntityRepository : JpaRepository<OrderEntity, String> {

    @Modifying
    @Query("update orders set status='paid' where razorpay_order_id=:razorpayOrderId", nativeQuery = true)
    fun paid(razorpayOrderId: String)
}