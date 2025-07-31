package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.DropShippingInfoResponse

data class ProductShippingResponse(
    val shippable: Boolean,
    val freeShipping: Boolean,
    val shippingClass: String?,
    val shippingRestrictions: List<String>,
    val handlingTime: Int,
    val packageType: String,
    val hazardousMaterial: Boolean,
    val requiresSignature: Boolean,
    val dropShipping: DropShippingInfoResponse?
)
