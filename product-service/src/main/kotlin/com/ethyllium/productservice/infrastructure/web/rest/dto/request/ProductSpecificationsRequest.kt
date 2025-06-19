package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.infrastructure.persistence.entity.ProductSpecificationsDocument

data class ProductSpecificationsRequest(
    val weight: WeightRequest? = null,
    val dimensions: DimensionsRequest? = null,
    val color: String? = null,
    val material: String? = null,
    val customAttributes: Map<String, Any> = emptyMap(),
    val technicalSpecs: Map<String, String> = emptyMap(),
    val certifications: List<CertificationRequest> = emptyList(),
    val compatibleWith: List<String> = emptyList()
) {
    fun toDocument() = ProductSpecificationsDocument(
        weight = weight?.toDocument(),
        dimensions = dimensions?.toDocument(),
        color = color,
        material = material,
        customAttributes = customAttributes,
        technicalSpecs = technicalSpecs,
        certifications = certifications.map { it.toDocument() },
        compatibleWith = compatibleWith
    )
}