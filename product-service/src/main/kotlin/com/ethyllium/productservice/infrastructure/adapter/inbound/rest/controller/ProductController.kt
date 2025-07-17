package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.annotations.RequireRoles
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.port.driver.ProductService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ApiResponse
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mapper.ProductMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products")
@Validated
@CrossOrigin(origins = ["*"], maxAge = 3600)
class ProductController(
    private val productService: ProductService, private val productMapper: ProductMapper
) {

    @PostMapping
    @RequireRoles(["SELLER"])
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<String>> {
        val product = productMapper.toDomain(request, sellerId)
        return productService.createProduct(product, sellerId).map { createdProduct ->
            val location = URI.create("/api/v1/products/${createdProduct.id}")
            ResponseEntity.created(location).body(createdProduct.id)
        }
    }

    @PutMapping("/{id}")
    @RequireRoles(["SELLER"])
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateProductRequest,
        @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.updateProduct(id, request)
            .map { ResponseEntity.ok(ApiResponse.success("Product with ID $id updated successfully")) }
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.message ?: "You do not have permission to update this product"))
                )
            }
    }

    @PatchMapping("/{id}/status")
    @RequireRoles(["SELLER"])
    fun updateProductStatus(
        @PathVariable id: String, @RequestParam status: ProductStatus, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.updateProductStatus(id, status)
            .map { message -> ResponseEntity.ok(ApiResponse.success(message, "Product status updated successfully")) }
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponse.error(
                            e.message ?: "You do not have permission to update this product's status"
                        )
                    )
                )
            }
    }

    @PatchMapping("/{id}/visibility")
    @RequireRoles(["SELLER"])
    fun updateProductVisibility(
        @PathVariable id: String,
        @RequestParam visibility: ProductVisibility,
        @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.updateProductVisibility(id, visibility).map { message ->
            ResponseEntity.ok(
                ApiResponse.success(
                    message, "Product visibility updated successfully"
                )
            )
        }.onErrorResume { e ->
            Mono.just(
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponse.error(
                        e.message ?: "You do not have permission to update this product's visibility"
                    )
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    @RequireRoles(["SELLER"])
    fun deleteProduct(
        @PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return productService.deleteProduct(id)
            .thenReturn(ResponseEntity.ok(ApiResponse.success(Unit, "Product deleted successfully")))
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.message ?: "You do not have permission to delete this product"))
                )
            }
    }

    @PostMapping("/{id}/archive")
    @RequireRoles(["SELLER"])
    fun archiveProduct(
        @PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.archiveProduct(id)
            .map { message -> ResponseEntity.ok(ApiResponse.success(message, "Product archived successfully")) }
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.message ?: "You do not have permission to archive this product"))
                )
            }
    }

    @PostMapping("/{id}/restore")
    @RequireRoles(["SELLER"])
    fun restoreProduct(
        @PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.restoreProduct(id)
            .map { message -> ResponseEntity.ok(ApiResponse.success(message, "Product restored successfully")) }
            .onErrorResume { e ->
                Mono.just(
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.message ?: "You do not have permission to restore this product"))
                )
            }
    }

    @PostMapping("/bulk")
    @RequireRoles(["SELLER"])
    fun createProductsBulk(
        @Valid @RequestBody requests: List<CreateProductRequest>, @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        val products = requests.map { productMapper.toDomain(it, sellerId) }
        return productService.createProductsBulk(products, sellerId).map { result ->
            ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "Products created successfully"))
        }
    }

    @PatchMapping("/bulk/status")
    @RequireRoles(["SELLER"])
    fun updateProductsStatusBulk(
        @RequestBody productIds: List<String>,
        @RequestParam status: ProductStatus,
        @RequestHeader("X-User-Id") sellerId: String
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return productService.updateProductsStatusBulk(productIds, status, sellerId)
            .map { result -> ResponseEntity.ok(ApiResponse.success(result, "Products status updated successfully")) }
    }

    @DeleteMapping("/bulk")
    @RequireRoles(["ADMIN"])
    fun deleteProductsBulk(
        @RequestBody productIds: List<String>
    ): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return productService.deleteProductsBulk(productIds)
            .thenReturn(ResponseEntity.ok(ApiResponse.success(Unit, "Products deleted successfully")))
    }
}