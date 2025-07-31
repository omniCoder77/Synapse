package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driver.BrandService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateBrandRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateBrandRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/brands")
class BrandController(
    private val brandService: BrandService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun createBrand(
        @RequestBody createBrandRequest: CreateBrandRequest,
        @RequestParam("file") file: FilePart?,
        authentication: Authentication
    ): Mono<ResponseEntity<Brand>> {
        return brandService.create(createBrandRequest.toBrand(ownerId = authentication.name), file)
            .map { createdBrand ->
                ResponseEntity.created(URI.create("/api/v1/search/brand/${createdBrand.id}")).body(createdBrand)
            }
    }

    @PostMapping("/{brandId}/upload-logo")
    fun uploadBrandLogo(
        @PathVariable brandId: String, @RequestPart("file") file: Mono<FilePart>, authentication: Authentication
    ): Mono<ResponseEntity<String>> {
        return file.flatMap { filePart ->
            if (filePart.filename().isEmpty()) {
                Mono.just(ResponseEntity.badRequest().body("File is empty"))
            } else {
                brandService.uploadBrandLogo(brandId, filePart, authentication.name).map { uploadedBrand ->
                    if (uploadedBrand) ResponseEntity.ok("Logo uploaded successfully")
                    else ResponseEntity.badRequest().body("Failed to upload logo")
                }
            }
        }.switchIfEmpty(
            Mono.just(ResponseEntity.badRequest().body("No file provided"))
        )
    }

    @PatchMapping("/{brandId}")
    fun updateBrand(
        @PathVariable brandId: String,
        @RequestBody updateBrandRequest: UpdateBrandRequest,
        authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        return brandService.update(
            brandId,
            updateBrandRequest.name,
            updateBrandRequest.description,
            updateBrandRequest.website,
            updateBrandRequest.slug,
            authentication.name
        ).map { updated ->
            if (updated) {
                ResponseEntity.ok(ApiResponse.success("Brand updated successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Brand not found or no changes were made."))
            }
        }
    }

    @DeleteMapping("/{brandId}")
    fun deleteBrand(
        @PathVariable brandId: String,
        authentication: Authentication
    ): Mono<ResponseEntity<ApiResponse<String>>> {
        logger.info(authentication.name)
        return brandService.delete(brandId, authentication.name).map { deleted ->
            if (deleted) {
                ResponseEntity.ok(ApiResponse.success("Brand deleted successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Brand not found or you don't have permission to delete it."))
            }
        }
    }
}