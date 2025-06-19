package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.domain.entity.DimensionUnit
import com.ethyllium.productservice.infrastructure.persistence.entity.DimensionsDocument
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull

data class DimensionsRequest(
    @field:NotNull(message = "Length is required") @field:DecimalMin(
        value = "0.1",
        message = "Length must be greater than 0"
    ) val length: Long,

    @field:NotNull(message = "Width is required") @field:DecimalMin(
        value = "0.1",
        message = "Width must be greater than 0"
    ) val width: Long,

    @field:NotNull(message = "Height is required") @field:DecimalMin(
        value = "0.1",
        message = "Height must be greater than 0"
    ) val height: Long,

    val unit: DimensionUnit = DimensionUnit.CM
) {
    fun toDocument() = DimensionsDocument(
        length = length,
        width = width,
        height = height,
        unit = unit.name
    )
}