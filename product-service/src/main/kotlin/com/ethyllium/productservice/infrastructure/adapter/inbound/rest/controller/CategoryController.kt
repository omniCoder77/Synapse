package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driver.CategoryService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateCategoryRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateCategoryRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun createCategory(
        @RequestPart("category") createCategoryRequest: CreateCategoryRequest,
        @RequestPart("file") file: MultipartFile?
    ): Mono<ResponseEntity<Category>> {
        return categoryService.create(createCategoryRequest.toCategory(), file).map { createdCategory ->
            ResponseEntity.created(URI.create("/api/v1/products/categories/${createdCategory.id}"))
                .body(createdCategory)
        }
    }

    @PatchMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: String,
        @RequestBody updateCategoryRequest: UpdateCategoryRequest
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

    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: String): Mono<ResponseEntity<String>> {
        return categoryService.delete(categoryId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete category")
            else ResponseEntity.ok("Category deleted successfully")
        }
    }
}