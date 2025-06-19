package com.ethyllium.searchservice.infrastructure.web.rest

import com.ethyllium.searchservice.application.dto.*
import com.ethyllium.searchservice.domain.service.ProductSearchService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ProductSearchController(
    private val productSearchService: ProductSearchService,
) {

    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) brand: List<String>?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @RequestParam(required = false) status: List<String>?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(required = false) color: List<String>?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<SearchResultDTO> {
        val results = productSearchService.searchProducts(
            query = query,
            category = category,
            brand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            status = status,
            tags = tags,
            color = color,
            pageable = pageable
        ).mapNotNull { it.toDTO() }

        return ResponseEntity.ok(SearchResultDTO(results, results.size.toLong()))
    }

    @GetMapping("/search/suggest")
    fun getSuggestions(
        @RequestParam term: String, @RequestParam(defaultValue = "5") limit: Int
    ): ResponseEntity<SuggestionsDTO> {
        val suggestions = productSearchService.autocompleteSuggestions(term, limit)
        return ResponseEntity.ok(SuggestionsDTO(suggestions))
    }

    @PostMapping("/search/advanced")
    fun advancedSearch(
        @RequestBody request: AdvancedSearchRequestDTO, @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<SearchResultDTO> {
        val results = productSearchService.searchProducts(
            query = request.query,
            filters = request.toFiltersMap(),
            category = pageable,
            brand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            status = status,
            tags = tags,
            color = color,
            pageable1 = pageable,
            pageable2 = pageable
        )
        return ResponseEntity.ok(SearchResultDTO.fromDomain(results, emptyMap()))
    }

    @GetMapping("/products/{id}")
    fun getProductDetails(@PathVariable id: String): ResponseEntity<ProductDetailDTO> {
        val product = productSearchService.getProductById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ProductDetailDTO.fromDomain(product))
    }

    @GetMapping("/products/{id}/similar")
    fun getSimilarProducts(
        @PathVariable id: String, @RequestParam(defaultValue = "4") limit: Int
    ): ResponseEntity<List<ProductSummaryDTO>> {
        val similar = productSearchService.findSimilarProducts(id, limit)
        return ResponseEntity.ok(similar.map { ProductSummaryDTO.fromDomain(it) })
    }

    @GetMapping("/search/popular")
    fun getPopularSearches(
        @RequestParam(defaultValue = "5") limit: Int, @RequestParam(defaultValue = "7d") timespan: String
    ): ResponseEntity<PopularSearchesDTO> {
        val popular = productSearchService.getPopularSearches(limit, timespan)
        return ResponseEntity.ok(PopularSearchesDTO.fromDomain(popular))
    }
}