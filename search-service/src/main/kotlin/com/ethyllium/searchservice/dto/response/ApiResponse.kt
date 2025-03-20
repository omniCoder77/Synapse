package com.ethyllium.searchservice.dto.response

import org.springframework.http.HttpStatus

sealed interface ApiResponse {
    data class Success<T>(val data: T) : ApiResponse
    data class Error(val message: String, val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) : ApiResponse

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(message: String, status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) = Error(message, status)
    }
}
