package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.port.driver.ProductService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ApiResponse
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.mapper.ProductMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products")
@Validated
@CrossOrigin(origins = ["*"], maxAge = 3600)
class ProductController(
    private val productService: ProductService, private val productMapper: ProductMapper,
) {

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest, authentication: Authentication
    ): Mono<ResponseEntity<String>> {
        val sellerId = authentication.name
        val product = productMapper.toDomain(request, sellerId)
        return productService.createProduct(product, sellerId).map { createdProduct ->
            val location = URI.create("/api/v1/products/${createdProduct.id}")
            ResponseEntity.created(location).body(createdProduct.id)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateProductRequest,
        authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun updateProductStatus(
        @PathVariable id: String, @RequestParam status: ProductStatus, authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun updateProductVisibility(
        @PathVariable id: String,
        @RequestParam visibility: ProductVisibility,
        authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun deleteProduct(
        @PathVariable id: String, authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun archiveProduct(
        @PathVariable id: String, authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun restoreProduct(
        @PathVariable id: String, authentication: Authentication
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
    @PreAuthorize("hasRole('SELLER')")
    fun createProductsBulk(
        @Valid @RequestBody requests: List<CreateProductRequest>, authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        val sellerId = authentication.name
        val products = requests.map { productMapper.toDomain(it, sellerId) }
        return productService.createProductsBulk(products, sellerId).map { result ->
            ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "Products created successfully"))
        }
    }

    @PatchMapping("/bulk/status")
    @PreAuthorize("hasRole('SELLER')")
    fun updateProductsStatusBulk(
        @RequestBody productIds: List<String>,
        @RequestParam status: ProductStatus,
        authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        val sellerId = authentication.name
        return productService.updateProductsStatusBulk(productIds, status, sellerId)
            .map { result -> ResponseEntity.ok(ApiResponse.success(result, "Products status updated successfully")) }
    }

    @DeleteMapping("/bulk")
    fun deleteProductsBulk(
        @RequestBody productIds: List<String>
    ): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return productService.deleteProductsBulk(productIds)
            .thenReturn(ResponseEntity.ok(ApiResponse.success(Unit, "Products deleted successfully")))
    }
}