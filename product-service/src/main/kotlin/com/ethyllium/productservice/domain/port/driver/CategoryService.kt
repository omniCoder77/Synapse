package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.Category
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface CategoryService {
    fun create(category: Category, file: FilePart?): Mono<Category>
    fun update(
        categoryId: String,
        name: String?,
        description: String?,
        slug: String?,
        parentId: String?
    ): Mono<Boolean>
    fun delete(categoryId: String): Mono<Boolean>
    fun uploadCategoryLogo(categoryId: String, file: FilePart): Mono<Boolean>
}