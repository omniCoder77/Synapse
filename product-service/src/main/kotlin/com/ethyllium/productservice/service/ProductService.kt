package com.ethyllium.productservice.service

import com.ethyllium.productservice.model.Product
import com.ethyllium.productservice.producer.MessageProducer
import com.ethyllium.productservice.util.KafkaTopics
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val mongoTemplate: MongoTemplate, private val messageProducer: MessageProducer
) {

    fun addProduct(product: Product): Product {
        val savedProduct = mongoTemplate.save(product)
        messageProducer.sendMessage(KafkaTopics.NEW_PRODUCT, product)
        return savedProduct
    }
}