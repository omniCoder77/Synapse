package com.synapse.orderservice.infrastructure.inbound.web.rest.dto

sealed interface ApiResponse {
    data class Success<T>(val data: T): ApiResponse
    data class Error(val message: String, val code: Int): ApiResponse
}