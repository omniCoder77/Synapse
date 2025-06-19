package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class DropShippingInfoResponse(
    val enabled: Boolean,
    val supplierId: String?,
    val supplierSku: String?,
    val supplierPrice: Long?
)
