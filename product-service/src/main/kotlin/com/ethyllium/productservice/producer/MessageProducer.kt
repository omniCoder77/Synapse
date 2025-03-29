package com.ethyllium.productservice.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MessageProducer {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String>? = null

    fun sendMessage(topic: String, message: Any) {
        kafkaTemplate!!.send(topic, objectMapper.writeValueAsString(message))
    }
}