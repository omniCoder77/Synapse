package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.PackageType

data class ProductShippingResponse(
    val shippable: Boolean,
    val freeShipping: Boolean,
    val shippingClass: String?,
    val shippingRestrictions: List<String>,
    val handlingTime: Int,
    val packageType: PackageType,
    val hazardousMaterial: Boolean,
    val requiresSignature: Boolean,
    val dropShipping: DropShippingInfoResponse?
)
