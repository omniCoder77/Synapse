package com.ethyllium.searchservice.application.dto

import com.ethyllium.searchservice.domain.model.Product

data class SearchResultDTO(
    val results: List<ProductSummaryDTO>, val total: Long
)

data class ProductSummaryDTO(
    val id: String,
    val name: String,
    val description: String,
    val price: ProductPriceDTO,
    val brand: BrandDTO,
    val category: CategoryDTO,
    val imageUrl: String?,
    val stockStatus: String,
    val rating: Double?
) {
    companion object {
        fun fromDomain(product: Product): ProductSummaryDTO {
            return ProductSummaryDTO(
                id = product.id,
                name = product.name,
                description = product.shortDescription ?: product.description,
                price = ProductPriceDTO.fromDomain(product.pricing),
                brand = BrandDTO.fromDomain(product.brand),
                category = CategoryDTO.fromDomain(product.category),
                imageUrl = product.media.primaryImageUrl,
                stockStatus = product.inventory.stockStatus.toString(),
                rating = product.averageRating
            )
        }
    }
}

data class ProductPriceDTO(
    val basePrice: Long, val salePrice: Long?, val currency: String
) {
    companion object {
        fun fromDomain(pricing: Product.SearchPricing): ProductPriceDTO {
            return ProductPriceDTO(
                basePrice = pricing.basePrice, salePrice = pricing.salePrice, currency = pricing.currency
            )
        }
    }
}