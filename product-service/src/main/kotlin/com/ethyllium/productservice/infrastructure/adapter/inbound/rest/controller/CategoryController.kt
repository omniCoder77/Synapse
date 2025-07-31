// src/main/kotlin/com/ethyllium/productservice/infrastructure/adapter/inbound/rest/controller/CategoryController.kt

package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driver.CategoryService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateCategoryRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateCategoryRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/category")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createCategory(
        @RequestBody createCategoryRequest: CreateCategoryRequest,
        @RequestPart("file", required = false) file: FilePart?
    ): Mono<ResponseEntity<Category>> {
        return categoryService.create(createCategoryRequest.toCategory(), file).map { createdCategory ->
            ResponseEntity.created(URI.create("/api/v1/products/categories/${createdCategory.id}"))
                .body(createdCategory)
        }
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateCategory(
        @PathVariable categoryId: String, @RequestBody updateCategoryRequest: UpdateCategoryRequest
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return categoryService.update(
            categoryId,
            updateCategoryRequest.name,
            updateCategoryRequest.description,
            updateCategoryRequest.slug,
            updateCategoryRequest.parentId
        ).map { updated ->
            if (updated) {
                ResponseEntity.ok(ApiResponse.success("Category updated successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category not found or no changes were made."))
            }
        }
    }

    @PostMapping("/{categoryId}/upload-logo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun uploadCategoryLogo(
        @PathVariable categoryId: String, @RequestPart("file") file: FilePart
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return categoryService.uploadCategoryLogo(categoryId, file).map { uploaded ->
            if (uploaded) {
                ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Failed to upload logo, category may not exist."))
            }
        }
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteCategory(@PathVariable categoryId: String): Mono<ResponseEntity<ApiResponse<String>>> {
        return categoryService.delete(categoryId).map { deleted ->
            if (deleted) {
                ResponseEntity.ok(ApiResponse.success("Category deleted successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Category not found."))
            }
        }
    }
}