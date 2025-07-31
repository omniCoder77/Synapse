package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driven.BrandRepository
import com.ethyllium.productservice.domain.port.driven.OutboxEntityRepository
import com.ethyllium.productservice.domain.port.driven.StorageService
import com.ethyllium.productservice.domain.port.driver.BrandService
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class BrandServiceImpl(
    private val brandRepository: BrandRepository,
    private val outboxEntityRepository: OutboxEntityRepository,
    private val storageService: StorageService
) : BrandService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val BRAND_LOGOS_STORAGE_PATH = "brand-logos"
        const val BRAND_LOGOS_URL_PATH = "brand-logos"
    }

    @Transactional
    override fun create(brand: Brand, file: FilePart?): Mono<Brand> {
        val brandWithLogoMono = if (file != null && file.filename().isNotEmpty()) {
            storageService.store(file, BRAND_LOGOS_STORAGE_PATH).map { filename ->
                    brand.apply { logoUrl = storageService.getFileUrl(filename, BRAND_LOGOS_URL_PATH) }
                }
        } else {
            Mono.just(brand)
        }

        return brandWithLogoMono.flatMap { brandToSave ->
            brandRepository.insert(brandToSave).flatMap { savedBrand ->
                    outboxEntityRepository.publishBrandCreated(savedBrand).thenReturn(savedBrand)
                }
        }
    }

    @Transactional
    override fun uploadBrandLogo(brandId: String, file: FilePart, ownerId: String): Mono<Boolean> {
        return storageService.store(file, BRAND_LOGOS_STORAGE_PATH).flatMap { filename ->
            val fileUrl = storageService.getFileUrl(filename, BRAND_LOGOS_URL_PATH)
            brandRepository.uploadLogo(brandId, fileUrl, ownerId).flatMap { success ->
                    if (success) {
                        outboxEntityRepository.publishBrandUpdated(brandId = brandId, fileUrl = fileUrl)
                            .thenReturn(true)
                    } else {
                        storageService.delete(filename, BRAND_LOGOS_STORAGE_PATH).thenReturn(false)
                    }
                }
        }
    }

    @Transactional
    override fun update(
        brandId: String, name: String?, description: String?, website: String?, slug: String?, ownerId: String
    ): Mono<Boolean> {
        return brandRepository.update(
            brandId = brandId, name = name, description = description, website = website, slug = slug, ownerId = ownerId
        ).flatMap { updated ->
            if (updated) {
                outboxEntityRepository.publishBrandUpdated(
                    brandId = brandId, description = description, website = website, slug = slug, name = name
                ).thenReturn(true)
            } else {
                Mono.just(false)
            }
        }
    }

    @Transactional
    override fun delete(brandId: String, ownerId: String): Mono<Boolean> {
        return brandRepository.delete(brandId, ownerId).flatMap { deleted ->
            if (deleted) {
                outboxEntityRepository.publishBrandDeleted(brandId).thenReturn(true)
            } else {
                Mono.just(false)
            }
        }
    }
}