package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driven.CategoryRepository
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driver.CategoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val eventPublisher: EventPublisher,
    @Value("\${file.category-image}") private val uploadDirPath: String
) : CategoryService {

    var uploadDir: Path = Path("")

    init {
        uploadDir = Path.of(uploadDirPath)
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    override fun create(category: Category, file: MultipartFile?): Mono<Category> {
        val fileUploadMono = file?.let {
            val fileExtension = it.originalFilename?.substringAfterLast('.', "")
            val filename = "${UUID.randomUUID()}.$fileExtension"
            val filePath = uploadDir.resolve(filename)
            category.imageUrl = "/category-images/$filename"
            Mono.just(it.transferTo(filePath.toFile()))
        } ?: Mono.empty()

        return categoryRepository.insert(category).doOnSuccess { cat ->
            eventPublisher.publishCategoryCreated(cat).subscribeOn(Schedulers.boundedElastic()).subscribe()
            fileUploadMono.subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    override fun update(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?
    ): Mono<Boolean> {
        return categoryRepository.update(categoryId, name, description, slug, parentId).doOnSuccess { updated ->
                if (updated) {
                    // TODO: eventPublisher.publishCategoryUpdated(categoryId, name, description, slug, parentId).subscribeOn(Schedulers.boundedElastic()).subscribe()
                }
            }
    }

    override fun delete(categoryId: String): Mono<Boolean> {
        return categoryRepository.delete(categoryId).doOnSuccess { deleted ->
            if (deleted) {
                // TODO: eventPublisher.publishCategoryDeleted(categoryId).subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        }
    }
}