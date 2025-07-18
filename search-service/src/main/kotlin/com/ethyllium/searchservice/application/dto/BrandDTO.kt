package com.ethyllium.searchservice.application.dto

import com.ethyllium.searchservice.domain.model.Product

data class BrandDTO(
    val id: String?,
    val name: String,
    val logoUrl: String?
) {
    companion object {
        fun fromDomain(brand: Product.SearchBrand): BrandDTO {
            return BrandDTO(
                id = null,
                name = brand.name,
                logoUrl = brand.logoUrl
            )
        }
    }
}

