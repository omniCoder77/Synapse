package com.ethyllium.searchservice.application.config

import com.ethyllium.searchservice.handler.OutboxEventHandler
import com.ethyllium.searchservice.model.OutboxEvent
import com.ethyllium.searchservice.ports.DeleteProduct
import com.ethyllium.searchservice.ports.EventFailed
import com.ethyllium.searchservice.ports.InsertProduct
import com.ethyllium.searchservice.repository.OutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.scheduling.annotation.Scheduled


@Configuration
class KafkaConsumerConfig(
    private val outboxEventHandler: OutboxEventHandler,
    private val insertProduct: InsertProduct,
    private val objectMapper: ObjectMapper,
    private val deleteProduct: DeleteProduct,
    private val eventFailed: EventFailed,
    private val outboxRepository: OutboxRepository
) {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "my-group-id"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @KafkaListener(topics = ["products-topic"], groupId = "search-service-group")
    fun handleProductEvent(
        @Payload payload: String, @Header(KafkaHeaders.RECEIVED_KEY) productId: String
    ) {
        val event = outboxEventHandler.toOutboxEvent(payload)
        try {
            if (outboxEventHandler.isDuplicate(event.outboxEventId)) return
            processEvent(event)
        } catch (e: Exception) {
            eventFailed.eventFailed(event)
        }
    }

    private fun processEvent(event: OutboxEvent) {
        when (event.eventType) {
            "PRODUCT_CREATED", "PRODUCT_UPDATED" -> insertProduct.insert(
                objectMapper.readValue(
                    event.payload, Product::class.java
                )
            )

            "PRODUCT_DELETED" -> deleteProduct.deleteProduct(event.aggregateId)
        }

    }

    @Scheduled(cron = "0 0/5 * * * ?")
    fun processEvents() {
        val events = outboxRepository.findOutboxEventsBySent(false)
        for (event in events) {
            processEvent(event)
        }
    }
}