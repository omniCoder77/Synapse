package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.domain.entity.PackageType
import com.ethyllium.productservice.infrastructure.persistence.entity.ProductShippingDocument
import jakarta.validation.constraints.Min

data class ProductShippingRequest(
    val shippable: Boolean = true,
    val freeShipping: Boolean = false,
    val shippingClass: String? = null,
    val shippingRestrictions: List<String> = emptyList(),

    @field:Min(value = 1, message = "Handling time must be at least 1 day") val handlingTime: Int = 1,

    val packageType: PackageType = PackageType.BOX,
    val hazardousMaterial: Boolean = false,
    val requiresSignature: Boolean = false,
    val dropShipping: DropShippingInfoRequest? = null
) {
    fun toDocument() = ProductShippingDocument(
        shippable = shippable,
        freeShipping = freeShipping,
        shippingClass = shippingClass,
        shippingRestrictions = shippingRestrictions,
        handlingTime = handlingTime,
        packageType = packageType.name,
        hazardousMaterial = hazardousMaterial,
        requiresSignature = requiresSignature,
        dropShipping = dropShipping?.toDocument()
    )
}