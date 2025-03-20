package com.ethyllium.searchservice.model

import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.util.UUID

@Document(indexName = "products")
data class Product(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Field(type = FieldType.Text)
    val name: String = "",

    @Field(type = FieldType.Text)
    val description: String = "",

    @Field(type = FieldType.Double)
    val price: Double = 0.0,

    @Field(type = FieldType.Keyword)
    val category: String = "",

    @Field(type = FieldType.Keyword)
    val brand: String = "",

    @Field(type = FieldType.Keyword)
    val tags: List<String> = emptyList(),

    @Field(type = FieldType.Double)
    val rating: Double = 0.0,

    @Field(type = FieldType.Object)
    val attributes: Map<String, String> = emptyMap()
)

