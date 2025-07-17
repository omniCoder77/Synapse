package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class ProductSpecificationsResponse(
    val weight: WeightResponse?,
    val dimensions: DimensionsResponse?,
    val color: String?,
    val material: String?,
    val customAttributes: Map<String, Any>,
    val technicalSpecs: Map<String, String>,
    val certifications: List<CertificationResponse>,
    val compatibleWith: List<String>
)
