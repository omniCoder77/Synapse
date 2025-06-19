package com.ethyllium.searchservice.application.dto

data class FacetsDTO(
    val categories: List<FacetItemDTO>,
    val brands: List<FacetItemDTO>,
    val priceRange: PriceRangeDTO,
    val specs: Map<String, List<FacetItemDTO>>
) {
    companion object {
        fun fromDomain(facets: Map<String, Any>): FacetsDTO {
            return FacetsDTO(
                categories = (facets["categories"] as? List<*>)?.filterIsInstance<FacetItemDTO>() ?: emptyList(),
                brands = (facets["brands"] as? List<*>)?.filterIsInstance<FacetItemDTO>() ?: emptyList(),
                priceRange = facets["priceRange"] as? PriceRangeDTO ?: PriceRangeDTO(0, 0),
                specs = (facets["specs"] as? Map<String, List<FacetItemDTO>>) ?: emptyMap()
            )
        }
    }
}

data class FacetItemDTO(
    val id: String? = null,
    val value: String,
    val label: String,
    val count: Int
)

data class PriceRangeDTO(
    val min: Long,
    val max: Long
)