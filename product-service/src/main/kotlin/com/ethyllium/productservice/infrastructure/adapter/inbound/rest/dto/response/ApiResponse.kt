package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: ErrorDetails? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, data = data, message = message)
        }

        fun <T> error(message: String, details: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorDetails(message = message, details = details)
            )
        }
    }
}

data class ErrorDetails(
    val message: String,
    val details: String? = null,
    val code: String? = null
)