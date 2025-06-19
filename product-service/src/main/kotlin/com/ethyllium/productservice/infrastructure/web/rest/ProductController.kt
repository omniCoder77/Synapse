package com.ethyllium.productservice.infrastructure.web.rest

import com.ethyllium.productservice.application.service.ProductService
import com.ethyllium.productservice.domain.annotations.RequireRoles
import com.ethyllium.productservice.domain.entity.ProductStatus
import com.ethyllium.productservice.domain.entity.ProductVisibility
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ApiResponse
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductSummaryResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/products")
@Validated
@CrossOrigin(origins = ["*"], maxAge = 3600)
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    @RequireRoles(["SELLER"])
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest,
        @RequestHeader("X-User-Id") sellerId: String
    ): ResponseEntity<ApiResponse<String>> {
        val productId = productService.createProduct(request, sellerId)
        val location = URI.create("/products/${productId}")
        return ResponseEntity.created(location).body(ApiResponse.success(productId, "Product created successfully"))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: String): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(ApiResponse.success(product))
    }

    @PutMapping("/{id}")
    @RequireRoles(["SELLER"])
    fun updateProduct(
        @PathVariable id: String, @Valid @RequestBody request: UpdateProductRequest, @RequestHeader("X-User-Id") sellerId: String
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"))
    }

    @PatchMapping("/{id}/status")
    @RequireRoles(["SELLER"])
    fun updateProductStatus(
        @PathVariable id: String, @RequestParam status: ProductStatus, @RequestHeader("X-User-Id") sellerId: String
    ): ResponseEntity<ApiResponse<String>> {
                if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        val product = productService.updateProductStatus(id, status)
        return ResponseEntity.ok(ApiResponse.success(product, "Product status updated successfully"))
    }

    @PatchMapping("/{id}/visibility")
    @RequireRoles(["SELLER"])
    fun updateProductVisibility(
        @PathVariable id: String, @RequestParam visibility: ProductVisibility, @RequestHeader("X-User-Id") sellerId: String
    ): ResponseEntity<ApiResponse<String>> {
                if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        val product = productService.updateProductVisibility(id, visibility)
        return ResponseEntity.ok(ApiResponse.success(product, "Product visibility updated successfully"))
    }

    @DeleteMapping("/{id}")
    @RequireRoles(["SELLER"])
    fun deleteProduct(@PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String): ResponseEntity<ApiResponse<Unit>> {
                if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        productService.deleteProduct(id)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Product deleted successfully"))
    }

    @PostMapping("/{id}/archive")
    @RequireRoles(["SELLER"])
    fun archiveProduct(@PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String): ResponseEntity<ApiResponse<String>> {
                if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        val product = productService.archiveProduct(id)
        return ResponseEntity.ok(ApiResponse.success(product, "Product archived successfully"))
    }

    @PostMapping("/{id}/restore")
    @RequireRoles(["SELLER"])
    fun restoreProduct(@PathVariable id: String, @RequestHeader("X-User-Id") sellerId: String): ResponseEntity<ApiResponse<String>> {
                if (!productService.isSellerOwner(sellerId, id)) return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("You do not have permission to update this product"))
        val product = productService.restoreProduct(id)
        return ResponseEntity.ok(ApiResponse.success(product, "Product restored successfully"))
    }

    @GetMapping("/{id}/exists")
    fun checkProductExists(@PathVariable id: String): ResponseEntity<ApiResponse<Boolean>> {
        val exists = productService.existsById(id)
        return ResponseEntity.ok(ApiResponse.success(exists))
    }

    @GetMapping("/sku/{sku}")
    fun getProductBySku(@PathVariable sku: String): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.getProductBySku(sku)
        return ResponseEntity.ok(ApiResponse.success(product))
    }

    @GetMapping("/seller/{sellerId}")
    fun getProductsBySeller(
        @PathVariable sellerId: String,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String,
        @RequestParam(required = false) status: ProductStatus?
    ): ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> {

        val sort = Sort.by(
            if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy
        )
        val pageable = PageRequest.of(page, size, sort)

        val products = productService.getProductsBySeller(sellerId, pageable, status)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @GetMapping("/category/{categoryId}")
    fun getProductsByCategory(
        @PathVariable categoryId: String,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> {

        val sort = Sort.by(
            if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy
        )
        val pageable = PageRequest.of(page, size, sort)

        val products = productService.getProductsByCategory(categoryId, pageable)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @GetMapping("/brand/{brandId}")
    fun getProductsByBrand(
        @PathVariable brandId: String,
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> {

        val sort = Sort.by(
            if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC, sortBy
        )
        val pageable = PageRequest.of(page, size, sort)

        val products = productService.getProductsByBrand(brandId, pageable)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @PostMapping("/bulk")
    @RequireRoles(["ADMIN"])
    fun createProductsBulk(
        @Valid @RequestBody requests: List<CreateProductRequest>
    ): ResponseEntity<ApiResponse<String>> {
        val products = productService.createProductsBulk(requests, "")
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(products, "Products created successfully"))
    }

    @PatchMapping("/bulk/status")
    @RequireRoles(["ADMIN"])
    fun updateProductsStatusBulk(
        @RequestBody productIds: List<String>, @RequestParam status: ProductStatus
    ): ResponseEntity<ApiResponse<String>> {
        val products = productService.updateProductsStatusBulk(productIds, status)
        return ResponseEntity.ok(ApiResponse.success(products, "Products status updated successfully"))
    }

    @DeleteMapping("/bulk")
    @RequireRoles(["ADMIN"])
    fun deleteProductsBulk(
        @RequestBody productIds: List<String>
    ): ResponseEntity<ApiResponse<Unit>> {
        productService.deleteProductsBulk(productIds)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Products deleted successfully"))
    }
}