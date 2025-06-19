package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class CertificationResponse(
    val name: String,
    val issuedBy: String,
    val certificateNumber: String?,
    val validFrom: Long?,
    val validTo: Long?
)
