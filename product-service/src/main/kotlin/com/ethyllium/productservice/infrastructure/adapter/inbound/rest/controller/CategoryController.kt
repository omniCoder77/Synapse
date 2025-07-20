package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driver.CategoryService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateCategoryRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateCategoryRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/category")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun createCategory(
        @RequestBody createCategoryRequest: CreateCategoryRequest,
        @RequestPart("file", required = false) file: FilePart?
    ): Mono<ResponseEntity<Category>> {
        return categoryService.create(createCategoryRequest.toCategory(), file).map { createdCategory ->
            ResponseEntity.created(URI.create("/api/v1/products/categories/${createdCategory.id}"))
                .body(createdCategory)
        }
    }

    // Update category data (no file)
    @PatchMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: String, @RequestBody updateCategoryRequest: UpdateCategoryRequest
    ): Mono<ResponseEntity<String>> {
        return categoryService.update(
            categoryId,
            updateCategoryRequest.name,
            updateCategoryRequest.description,
            updateCategoryRequest.slug,
            updateCategoryRequest.parentId
        ).map { updatedCategory ->
            if (!updatedCategory) ResponseEntity.badRequest().body("Failed to update category")
            else ResponseEntity.ok("Category updated successfully")
        }
    }

    @PostMapping("/{categoryId}/upload-logo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadCategoryLogo(
        @PathVariable categoryId: String, @RequestPart("file") file: FilePart
    ): Mono<ResponseEntity<String>> {
        return categoryService.uploadCategoryLogo(categoryId, file).map { uploaded ->
            if (uploaded) ResponseEntity.ok("Logo uploaded successfully")
            else ResponseEntity.badRequest().body("Failed to upload logo")
        }
    }

    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: String): Mono<ResponseEntity<String>> {
        return categoryService.delete(categoryId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete category")
            else ResponseEntity.ok("Category deleted successfully")
        }
    }
}