package com.ethyllium.productservice.infrastructure.adapter.inbound.grpc

import io.grpc.Status
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.grpc.server.exception.GrpcExceptionHandler

@Configuration
class GlobalGrpcExceptionHandler {
    @Bean
    fun globalInterceptor(): GrpcExceptionHandler = GrpcExceptionHandler { exception ->
        when (exception) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(exception.message).asException()
            else -> null
        }
    }
}