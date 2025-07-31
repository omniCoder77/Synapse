package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.Brand
import reactor.core.publisher.Mono

interface BrandRepository {
    fun insert(brand: Brand): Mono<Brand>
    fun uploadLogo(brandId: String, fileUrl: String, ownerId: String): Mono<Boolean>
    fun update(brandId: String, name: String?, description: String?, website: String?, slug: String?, ownerId: String): Mono<Boolean>
    fun delete(brandId: String, ownerId: String) : Mono<Boolean>
}