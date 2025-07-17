package com.ethyllium.productservice.infrastructure.adapter.inbound.rest

import com.ethyllium.productservice.domain.exception.BrandDuplicateException
import com.ethyllium.productservice.domain.exception.InsufficientAuthenticationException
import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.exception.ProductNotFoundException
import com.ethyllium.productservice.domain.exception.ProductValidationException
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(
        ex: ProductNotFoundException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Product not found: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = ex.message ?: "Product not found", details = "The requested product could not be found"
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BrandDuplicateException::class)
    fun handleBrandDuplicate(
        ex: ProductNotFoundException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Brand Duplicate: {}", ex.message)

        val errorResponse =ApiResponse.error<Any>(
            message = ex.message ?: "Brand already exists",
            details = "A brand with the same identifier already exists"
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(ProductDuplicateException::class)
    fun handleProductDuplicate(
        ex: ProductDuplicateException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Duplicate product: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = ex.message ?: "Product already exists",
            details = "A product with the same SKU or identifier already exists"
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(ProductValidationException::class)
    fun handleProductValidation(
        ex: ProductValidationException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Product validation error: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = ex.message ?: "Product validation failed", details = ex.details
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Validation errors: {}", ex.message)

        val errors = ex.bindingResult.allErrors.map { error ->
            when (error) {
                is FieldError -> "${error.field}: ${error.defaultMessage}"
                else -> error.defaultMessage ?: "Validation error"
            }
        }

        val errorResponse = ApiResponse.error<Any>(
            message = "Validation failed", details = errors.joinToString("; ")
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Constraint violation: {}", ex.message)

        val errors = ex.constraintViolations.map { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }

        val errorResponse = ApiResponse.error<Any>(
            message = "Validation failed", details = errors.joinToString("; ")
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Invalid request body: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Invalid request body", details = "The request body is malformed or contains invalid data"
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Type mismatch: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Invalid parameter type",
            details = "Parameter '${ex.name}' should be of type ${ex.requiredType?.simpleName}"
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(
        ex: MissingServletRequestParameterException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Missing parameter: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Missing required parameter", details = "Required parameter '${ex.parameterName}' is missing"
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKey(
        ex: DuplicateKeyException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Duplicate key error: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Duplicate entry", details = "A record with the same unique identifier already exists"
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        ex: AccessDeniedException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Access denied: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Access denied", details = "You don't have permission to perform this operation"
        )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleInsufficientAuthentication(
        ex: InsufficientAuthenticationException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Insufficient authentication: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Authentication required", details = "You must be authenticated to perform this operation"
        )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
    ): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Illegal argument: {}", ex.message)

        val errorResponse = ApiResponse.error<Any>(
            message = "Invalid argument", details = ex.message
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ApiResponse<Any>> {
        logger.error("ResponseStatusException: ${ex.reason}", ex)

        return ResponseEntity.status(ex.statusCode).body(ApiResponse.error(ex.reason ?: "Request failed"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        logger.error("Unexpected error", ex)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal server error"))
    }
}