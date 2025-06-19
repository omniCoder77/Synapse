package com.ethyllium.productservice.domain.exception
class ProductValidationException(
    message: String,
    val details: String? = null
) : RuntimeException(message)