package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.Category
import reactor.core.publisher.Mono

interface CategoryRepository {
    fun insert(category: Category): Mono<Category>
    fun update(categoryId: String, name: String?, description: String?, slug: String?, parentId: String?): Mono<Boolean>
    fun update(categoryId: String, imageUrl: String): Mono<Boolean>
    fun delete(categoryId: String): Mono<Boolean>
}