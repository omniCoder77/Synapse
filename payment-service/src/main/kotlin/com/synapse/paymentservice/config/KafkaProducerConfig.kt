package com.synapse.paymentservice.config

import com.synapse.paymentservice.domain.event.DomainEvent
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    @Value("\${transaction.id.prefix}") private val transactionIdPrefix: String,
    @Value("\${spring.kafka.bootstrap-servers}") private val kafkaBootstrapServers: String,
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, DomainEvent> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        val factory = DefaultKafkaProducerFactory<String, DomainEvent>(configProps)
        factory.setTransactionIdPrefix(transactionIdPrefix)
        return factory
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, DomainEvent> {
        return KafkaTemplate(producerFactory())
    }
}
