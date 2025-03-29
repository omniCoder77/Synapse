package com.ethyllium.searchservice.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MessageConsumer {
    @KafkaListener(topics = ["my-topic"], groupId = "my-group-id")
    fun listen(message: String) {
        println("Received message: $message")
    }
}