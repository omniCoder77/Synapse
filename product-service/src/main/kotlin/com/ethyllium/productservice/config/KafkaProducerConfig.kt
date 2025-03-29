package com.ethyllium.productservice.config

import com.ethyllium.productservice.repository.OutboxRepository
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit


@Configuration
class KafkaProducerConfig(
    private val outboxRepository: OutboxRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.ACKS_CONFIG] = "all"
        configProps[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        configProps[ProducerConfig.RETRIES_CONFIG] = 3
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Transactional
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    fun publishOutboxEvents(kafkaTemplate: KafkaTemplate<String, String>) {
        val unsentEvents = outboxRepository.findBySent(false)
        unsentEvents.forEach { event ->
            kafkaTemplate.send("products-topic", event.aggregateId, event.payload).whenComplete { _, ex ->
                if (ex == null) {
                    event.sent = true
                    outboxRepository.sentEvent(event.outboxEventId)
                    logger.info("Event sent: ${event.outboxEventId}")
                } else {
                    logger.error("Failed to send event: ${event.outboxEventId}", ex)
                }
            }
        }
    }
}