package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("temp_seller")
data class TempSellerDocument(
    @Id val tempSellerId: ObjectId = ObjectId.get(), val email: String, val role: List<String>, val phoneNumber: String, val name: String, val sellerId: String
)
