package com.ethyllium.productservice.service

import com.ethyllium.productservice.model.Product
import com.ethyllium.productservice.ports.SellerExists
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class SellerService(private val mongoTemplate: MongoTemplate) : SellerExists {
    override fun sellerExists(sellerId: String): Boolean {
        return mongoTemplate.exists(
            Query.query(Criteria.where(Product::sellerId.name).`is`(sellerId)),
            Product::class.java
        )
    }
}