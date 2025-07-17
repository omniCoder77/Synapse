package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.Brand
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono

interface BrandService {
    fun create(brand: Brand, file: MultipartFile): Mono<Brand>
    fun uploadBrandLogo(brandId: String, file: MultipartFile): Mono<Boolean>
    fun update(
        brandId: String,
        name: String? = null,
        description: String? = null,
        website: String? = null,
        slug: String? = null,
    ): Mono<Boolean>

    fun delete(brandId: String): Mono<Boolean>
}