package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.dto

sealed interface ApiResponse {
    data class Success<T>(val data: T) : ApiResponse
    data class Error(val error: String) : ApiResponse
}