package com.synapse.orderservice.infrastructure.output.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaOrderEntityRepository: JpaRepository<OrderEntity, String>