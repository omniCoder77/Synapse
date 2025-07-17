package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.domain.model.WeightUnit
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.WeightDocument
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull

data class WeightRequest(
    @field:NotNull(message = "Weight value is required") @field:DecimalMin(
        value = "0.001",
        message = "Weight must be greater than 0"
    ) val value: Long,

    val unit: WeightUnit = WeightUnit.KG
) {
    fun toDocument() = WeightDocument(
        value = value,
        unit = unit.name
    )
}