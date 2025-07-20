package com.ethyllium.searchservice.infrastructure.web.rest

import com.ethyllium.searchservice.application.dto.PopularSearchesDTO
import com.ethyllium.searchservice.application.dto.ProductDetailDTO
import com.ethyllium.searchservice.application.dto.SearchResultDTO
import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.domain.service.ProductSearchService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1")
class ProductSearchController(
    private val productSearchService: ProductSearchService,
) {

    @GetMapping("/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchProducts(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) category: String? = null,
        @RequestParam(required = false) brand: List<String>? = null,
        @RequestParam(required = false) minPrice: Double? = null,
        @RequestParam(required = false) maxPrice: Double? = null,
        @RequestParam(required = false) status: List<String>? = null,
        @RequestParam(required = false) tags: List<String>? = null,
        @RequestParam(required = false) color: List<String>? = null,
        @PageableDefault(size = 20) pageable: Pageable
    ): Mono<SearchResultDTO> {
        return productSearchService.searchProducts(
            query = query,
            category = category,
            brand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            status = status,
            tags = tags,
            color = color,
            pageable = pageable
        ).map { searchResult ->
            SearchResultDTO(
                results = searchResult.content.mapNotNull { it.toDTO() }, total = searchResult.totalElements
            )
        }
    }

    @GetMapping("/products/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProductDetails(@PathVariable id: String): Mono<ProductDetailDTO> {
        return productSearchService.getProductById(id).map { product -> ProductDetailDTO.fromDomain(product) }
    }

    @GetMapping("/products/{id}/similar", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSimilarProducts(
        @PathVariable id: String, @RequestParam(defaultValue = "4") limit: Int
    ): Flux<Product> {
        return productSearchService.findSimilarProducts(id, limit)
    }

    @GetMapping("/search/popular", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPopularSearches(
        @RequestParam(defaultValue = "5") limit: Int, @RequestParam(defaultValue = "7d") timespan: String
    ): Mono<PopularSearchesDTO> {
        return productSearchService.getPopularSearches(limit, timespan).collectList()
            .map { popular -> PopularSearchesDTO.fromDomain(popular) }
    }
}