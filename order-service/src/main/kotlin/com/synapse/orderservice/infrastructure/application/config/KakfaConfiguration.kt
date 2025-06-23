package com.synapse.orderservice.infrastructure.application.config

import com.synapse.orderservice.infrastructure.output.kafka.dto.OrderCreated
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KakfaConfiguration {
    @Bean
    fun producerFactory(): ProducerFactory<String, OrderCreated> {
        val config: MutableMap<String, Any> = HashMap()

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)

        return DefaultKafkaProducerFactory(config)
    }


    @Bean
    fun kafkaTemplate(): KafkaTemplate<String?, OrderCreated> {
        return KafkaTemplate(producerFactory())
    }
}