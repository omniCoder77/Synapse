package com.synapse.orderservice.infrastructure.web.rest

import com.synapse.orderservice.domain.exception.OrderNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException::class)
    fun handleOrderNotFoundException(e: OrderNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = e.message, status = 404
        )
        return ResponseEntity(errorResponse, org.springframework.http.HttpStatus.NOT_FOUND)
    }
}

data class ErrorResponse(val status: Int, val message: String)