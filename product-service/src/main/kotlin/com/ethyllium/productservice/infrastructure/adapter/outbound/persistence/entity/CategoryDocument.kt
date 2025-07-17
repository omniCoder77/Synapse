package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity

import com.ethyllium.productservice.domain.model.Category
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

data class CategoryDocument(
    @Id val id: String? = null,
    val name: String,
    val description: String? = null,
    @Indexed val parentId: String? = null,
    @Indexed(unique = true) val slug: String,
    @Indexed val level: Int = 0,
    val path: String,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Category = Category(
        id = this.id,
        name = this.name,
        description = this.description,
        parentId = this.parentId,
        slug = this.slug,
        level = this.level,
        path = this.path,
        imageUrl = this.imageUrl
    )
}

fun Category.toDocument(): CategoryDocument = CategoryDocument(
    id = this.id,
    name = this.name,
    description = this.description,
    parentId = this.parentId,
    slug = this.slug,
    level = this.level,
    path = this.path,
    imageUrl = this.imageUrl
)