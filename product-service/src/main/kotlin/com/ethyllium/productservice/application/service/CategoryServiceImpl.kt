package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driven.CategoryRepository
import com.ethyllium.productservice.domain.port.driven.OutboxEntityRepository
import com.ethyllium.productservice.domain.port.driven.StorageService
import com.ethyllium.productservice.domain.port.driver.CategoryService
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val outboxEntityRepository: OutboxEntityRepository,
    private val storageService: StorageService
) : CategoryService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val CATEGORY_LOGOS_STORAGE_PATH = "category-logos"
        const val CATEGORY_LOGOS_URL_PATH = "category-logos"
    }

    override fun create(category: Category, file: FilePart?): Mono<Category> {
        val categoryWithLogoMono = if (file != null && file.filename().isNotEmpty()) {
            storageService.store(file, CATEGORY_LOGOS_STORAGE_PATH).map { filename ->
                category.apply { imageUrl = storageService.getFileUrl(filename, CATEGORY_LOGOS_URL_PATH) }
            }
        } else {
            Mono.just(category)
        }

        return categoryWithLogoMono.flatMap { categoryToSave ->
            categoryRepository.insert(categoryToSave)
                .flatMap { savedCategory ->
                    outboxEntityRepository.publishCategoryCreated(savedCategory)
                        .thenReturn(savedCategory)
                }
        }
    }

    override fun update(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?
    ): Mono<Boolean> {
        return categoryRepository.update(categoryId, name, description, slug, parentId)
            .flatMap { updated ->
                if (updated) {
                    outboxEntityRepository.publishCategoryUpdated(categoryId, name, description, slug, parentId, null)
                        .thenReturn(true)
                } else {
                    Mono.just(false)
                }
            }
    }

    override fun delete(categoryId: String): Mono<Boolean> {
        return categoryRepository.delete(categoryId).flatMap { deleted ->
            if (deleted) {
                outboxEntityRepository.publishCategoryDeleted(categoryId).thenReturn(true)
            } else {
                Mono.just(false)
            }
        }
    }

    override fun uploadCategoryLogo(
        categoryId: String, file: FilePart
    ): Mono<Boolean> {
        return storageService.store(file, CATEGORY_LOGOS_STORAGE_PATH).flatMap { filename ->
            val imageUrl = storageService.getFileUrl(filename, CATEGORY_LOGOS_URL_PATH)
            categoryRepository.update(categoryId, imageUrl).flatMap { success ->
                if (success) {
                    outboxEntityRepository.publishCategoryUpdated(
                        categoryId = categoryId,
                        name = null,
                        description = null,
                        slug = null,
                        parentId = null,
                        imageUrl = imageUrl
                    ).thenReturn(true)
                } else {
                    // If DB update fails, attempt to clean up the orphaned file
                    storageService.delete(filename, CATEGORY_LOGOS_STORAGE_PATH)
                        .doOnSuccess { deleted -> if (deleted) logger.info("Cleaned up orphaned file: $filename") }
                        .thenReturn(false) // Return false for the overall operation
                }
            }
        }
    }
}