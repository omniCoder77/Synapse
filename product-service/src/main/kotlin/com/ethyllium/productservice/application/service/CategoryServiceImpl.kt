package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driven.CategoryRepository
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.domain.port.driven.StorageService
import com.ethyllium.productservice.domain.port.driver.CategoryService
import org.slf4j.LoggerFactory
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val eventPublisher: EventPublisher,
    private val storageService: StorageService
) : CategoryService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val CATEGORY_LOGOS_STORAGE_PATH = "category-logos"
        const val CATEGORY_LOGOS_URL_PATH = "category-logos"
    }

    override fun create(category: Category, file: FilePart?): Mono<Category> {
        return if (file != null && file.filename().isNotEmpty()) {
            storageService.store(file, CATEGORY_LOGOS_STORAGE_PATH).map { filename ->
                category.apply { imageUrl = storageService.getFileUrl(filename, CATEGORY_LOGOS_URL_PATH) }
            }.flatMap { updatedBrand ->
                categoryRepository.insert(updatedBrand)
            }.doOnSuccess { savedBrand ->
                Mono.just {
                    eventPublisher.publishCategoryCreated(category)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        } else {
            categoryRepository.insert(category).doOnSuccess { savedBrand ->
                Mono.just {
                    eventPublisher.publishCategoryCreated(category)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()

            }
        }
    }

    override fun update(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?
    ): Mono<Boolean> {
        return categoryRepository.update(categoryId, name, description, slug, parentId).doOnSuccess { updated ->
            if (updated) {
                Mono.fromRunnable<Void> {
                    eventPublisher.publishCategoryUpdated(categoryId, name, description, slug, parentId)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        }
    }

    override fun delete(categoryId: String): Mono<Boolean> {
        return categoryRepository.delete(categoryId).doOnSuccess { deleted ->
            if (deleted) {
                eventPublisher.publishCategoryDeleted(categoryId)
            }
        }
    }

    override fun uploadCategoryLogo(
        categoryId: String, file: FilePart
    ): Mono<Boolean> {
        return storageService.store(file, CATEGORY_LOGOS_STORAGE_PATH).flatMap { filename ->
                val imageUrl = storageService.getFileUrl(filename, CATEGORY_LOGOS_URL_PATH)
                categoryRepository.update(categoryId, imageUrl).doOnSuccess { success ->
                        if (success) {
                            Mono.fromRunnable<Void> {
                                eventPublisher.publishCategoryUpdated(
                                    categoryId = categoryId,
                                    name = null,
                                    description = null,
                                    slug = null,
                                    parentId = null,
                                    imageUrl = imageUrl
                                )
                            }.subscribeOn(Schedulers.boundedElastic()).subscribe()
                        } else {
                            storageService.delete(filename, CATEGORY_LOGOS_STORAGE_PATH).subscribe(
                                    { deleted -> if (deleted) logger.info("Cleaned up file after DB failure: $filename") },
                                    { error -> logger.error("Failed to cleanup file: $filename", error) })
                        }
                    }
            }
    }
}