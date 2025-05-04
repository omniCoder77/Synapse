package com.ethyllium.authservice.application.dto.response

sealed interface ApiResponse {
    data class Success<T>(val data: T) : ApiResponse
    data class Error(val error: String) : ApiResponse
}