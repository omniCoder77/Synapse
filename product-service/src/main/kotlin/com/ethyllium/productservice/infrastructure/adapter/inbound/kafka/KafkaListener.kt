package com.ethyllium.productservice.infrastructure.adapter.inbound.kafka

import com.ethyllium.productservice.domain.port.driven.SellerRepository
import com.ethyllium.productservice.infrastructure.adapter.inbound.kafka.entities.OutboxEvent
import com.ethyllium.productservice.infrastructure.adapter.inbound.kafka.entities.SellerRegisteredEvent
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaListener(private val sellerRepository: SellerRepository) {

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["auth-.public.outbox"], groupId = "product-service-consumer")
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OutboxEvent::class.java)
        if (event.after.eventType == "SellerRegisteredEvent") {
            logger.info("{}", event)
            val sellerRegisteredEvent = objectMapper.readValue(event.after.payload, SellerRegisteredEvent::class.java)
            sellerRepository.addTempSeller(sellerRegisteredEvent)
        }
    }

}