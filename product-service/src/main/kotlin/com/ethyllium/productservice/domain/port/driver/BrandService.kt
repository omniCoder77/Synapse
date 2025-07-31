package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.Brand
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface BrandService {
    fun create(brand: Brand, file: FilePart?): Mono<Brand>
    fun uploadBrandLogo(brandId: String, file: FilePart, ownerId:String): Mono<Boolean>
    fun update(
        brandId: String,
        name: String? = null,
        description: String? = null,
        website: String? = null,
        slug: String? = null,
        ownerId: String
    ): Mono<Boolean>

    fun delete(brandId: String, ownerId: String): Mono<Boolean>
}