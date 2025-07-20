package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driver.BrandService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateBrandRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateBrandRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/products/brands")
class BrandController(
    private val brandService: BrandService
) {

    @PostMapping
    fun createBrand(
        @RequestBody createBrandRequest: CreateBrandRequest, @RequestParam("file") file: FilePart?
    ): Mono<ResponseEntity<Brand>> {
        return brandService.create(createBrandRequest.toBrand(), file).map { createdBrand ->
            ResponseEntity.created(URI.create("/api/v1/search/brand/${createdBrand.id}")).body(createdBrand)
        }
    }

    @PostMapping("/{brandId}/upload-logo")
    fun uploadBrandLogo(
        @PathVariable brandId: String, @RequestPart("file") file: Mono<FilePart>
    ): Mono<ResponseEntity<String>> {
        return file.flatMap { filePart ->
            if (filePart.filename().isEmpty()) {
                Mono.just(ResponseEntity.badRequest().body("File is empty"))
            } else {
                brandService.uploadBrandLogo(brandId, filePart).map { uploadedBrand ->
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
        @PathVariable brandId: String, @RequestBody updateBrandRequest: UpdateBrandRequest
    ): Mono<ResponseEntity<String>> {
        return brandService.update(
            brandId,
            updateBrandRequest.name,
            updateBrandRequest.description,
            updateBrandRequest.website,
            updateBrandRequest.slug
        ).map { updatedBrand ->
            if (!updatedBrand) ResponseEntity.badRequest().body("Failed to update brand")
            else ResponseEntity.ok("Brand updated successfully")
        }
    }

    @DeleteMapping("/{brandId}")
    fun deleteBrand(@PathVariable brandId: String): Mono<ResponseEntity<String>> {
        return brandService.delete(brandId).map { deleted ->
            if (!deleted) ResponseEntity.badRequest().body("Failed to delete brand")
            else ResponseEntity.ok("Brand deleted successfully")
        }
    }
}