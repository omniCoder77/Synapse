package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class DropShippingInfoResponse(
    val enabled: Boolean,
    val supplierId: String?,
    val supplierSku: String?,
    val supplierPrice: Long?
)
