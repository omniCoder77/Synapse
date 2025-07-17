package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driven.BrandRepository
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driver.BrandService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

@Component
class BrandServiceImpl(
    private val brandRepository: BrandRepository,
    private val eventPublisher: EventPublisher,
    @Value("\${file.brand-logo}") private val uploadDirPath: String
) : BrandService {
    var uploadDir: Path = Path("")

    init {
        uploadDir = Path.of(uploadDirPath)
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    override fun create(brand: Brand, file: MultipartFile): Mono<Brand> {
        val fileExtension = file.originalFilename?.substringAfterLast('.', "")
        val filename = "${UUID.randomUUID()}.${fileExtension}"
        val filePath = uploadDir.resolve(filename)

        val fileUploadMono = Mono.just(file.transferTo(filePath.toFile()))
        val fileUrl = "/brand-logos/$filename"
        return brandRepository.insert(brand.apply { this.logoUrl = fileUrl }).doOnSuccess { br ->
            eventPublisher.publishBrandCreated(brand).subscribeOn(
                Schedulers.boundedElastic()
            ).subscribe()
            fileUploadMono.subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    override fun uploadBrandLogo(brandId: String, file: MultipartFile): Mono<Boolean> {
        val fileExtension = file.originalFilename?.substringAfterLast('.', "")
        val filename = "${UUID.randomUUID()}.${fileExtension}"
        val filePath = uploadDir.resolve(filename)

        val fileUploadMono = Mono.just(file.transferTo(filePath.toFile()))

        val fileUrl = "/brand-logos/$filename"

        return brandRepository.uploadLogo(brandId, fileUrl).doOnSuccess {
            fileUploadMono.subscribeOn(Schedulers.boundedElastic()).subscribe()
            eventPublisher.publishBrandUpdated(brandId = brandId, fileUrl = fileUrl).subscribeOn(
                Schedulers.boundedElastic()
            ).subscribe()
        }
    }

    override fun update(
        brandId: String, name: String?, description: String?, website: String?, slug: String?
    ): Mono<Boolean> {
        return brandRepository.update(
            brandId = brandId, name = name, description = description, website = website, slug = slug
        ).map { updatedBrand ->
            if (updatedBrand) {
                eventPublisher.publishBrandUpdated(
                    brandId = brandId, description = description, website = website, slug = slug
                ).subscribeOn(Schedulers.boundedElastic()).subscribe()
                updatedBrand
            } else updatedBrand
        }
    }

    override fun delete(brandId: String): Mono<Boolean> {
        return brandRepository.delete(brandId).doOnSuccess { deleted ->
            if (deleted) {
                eventPublisher.publishBrandDeleted(brandId).subscribeOn(Schedulers.boundedElastic()).subscribe()
            } else deleted
        }.subscribeOn(Schedulers.boundedElastic())
    }
}