package com.ethyllium.productservice.infrastructure.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "categories")
@CompoundIndexes(
    CompoundIndex(name = "parent_level_idx", def = "{'parentId': 1, 'level': 1}")
)
data class CategoryDocument(
    @Id val id: String,
    @TextIndexed val name: String,
    val description: String? = null,
    @Indexed val parentId: String? = null,
    @Indexed(unique = true) val slug: String,
    @Indexed val level: Int = 0,
    val path: String,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)