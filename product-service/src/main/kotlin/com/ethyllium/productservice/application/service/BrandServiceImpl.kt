package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driven.BrandRepository
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driven.StorageService
import com.ethyllium.productservice.domain.port.driver.BrandService
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class BrandServiceImpl(
    private val brandRepository: BrandRepository,
    private val eventPublisher: EventPublisher,
    private val storageService: StorageService
) : BrandService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val BRAND_LOGOS_STORAGE_PATH = "brand-logos"
        const val BRAND_LOGOS_URL_PATH = "brand-logos"
    }

    override fun create(brand: Brand, file: FilePart?): Mono<Brand> {
        return if (file != null && file.filename().isNotEmpty()) {
            storageService.store(file, BRAND_LOGOS_STORAGE_PATH).map { filename ->
                brand.apply { logoUrl = storageService.getFileUrl(filename, BRAND_LOGOS_URL_PATH) }
            }.flatMap { updatedBrand ->
                brandRepository.insert(updatedBrand)
            }.doOnSuccess { savedBrand ->
                Mono.just {
                    eventPublisher.publishBrandCreated(brand)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        } else {
            brandRepository.insert(brand).doOnSuccess { savedBrand ->
                Mono.just {
                    eventPublisher.publishBrandCreated(brand)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()

            }
        }
    }

    override fun uploadBrandLogo(brandId: String, file: FilePart): Mono<Boolean> {
        return storageService.store(file, BRAND_LOGOS_STORAGE_PATH).flatMap { filename ->
            val fileUrl = storageService.getFileUrl(filename, BRAND_LOGOS_URL_PATH)
            brandRepository.uploadLogo(brandId, fileUrl).doOnSuccess { success ->
                if (success) {
                    Mono.just {
                        eventPublisher.publishBrandUpdated(brandId = brandId, fileUrl = fileUrl)
                    }.subscribeOn(Schedulers.boundedElastic())
                        .subscribe({ }, { error -> logger.error("Failed to publish brand updated event", error) })
                } else {
                    storageService.delete(filename, BRAND_LOGOS_STORAGE_PATH).subscribe({ deleted ->
                        if (deleted) logger.info("Cleaned up file after DB failure: $filename")
                    }, { error -> logger.error("Failed to cleanup file: $filename", error) })
                }
            }
        }
    }

    override fun update(
        brandId: String, name: String?, description: String?, website: String?, slug: String?
    ): Mono<Boolean> {
        return brandRepository.update(
            brandId = brandId, name = name, description = description, website = website, slug = slug
        ).map { updatedBrand ->
            if (updatedBrand) {
                Mono.just(
                    eventPublisher.publishBrandUpdated(
                        brandId = brandId, description = description, website = website, slug = slug
                    )
                ).subscribeOn(Schedulers.boundedElastic()).subscribe()
                updatedBrand
            } else updatedBrand
        }
    }

    override fun delete(brandId: String): Mono<Boolean> {
        return brandRepository.delete(brandId).doOnSuccess { deleted ->
            if (deleted) {
                Mono.just(eventPublisher.publishBrandDeleted(brandId)).subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            } else deleted
        }.subscribeOn(Schedulers.boundedElastic())
    }
}