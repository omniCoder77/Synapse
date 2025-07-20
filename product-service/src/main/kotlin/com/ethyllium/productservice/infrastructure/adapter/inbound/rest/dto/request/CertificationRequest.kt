package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.CertificationDocument
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CertificationRequest(
    @field:NotBlank(message = "Certification name is required") val name: String,

    @field:NotBlank(message = "Issuing authority is required") val issuedBy: String,

    val certificateNumber: String, val validFrom: Long, val validTo: Long
) {
    fun toDocument() = CertificationDocument(
        name = name,
        issuedBy = issuedBy,
        certificateNumber = certificateNumber,
        validFrom = Instant.ofEpochMilli(validFrom),
        validTo = Instant.ofEpochMilli(validTo)
    )
}