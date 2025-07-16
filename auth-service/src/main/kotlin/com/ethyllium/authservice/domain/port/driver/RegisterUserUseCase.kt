package com.ethyllium.authservice.domain.port.driver

import com.ethyllium.authservice.domain.model.RegisterResult
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.RegisterRequest
import reactor.core.publisher.Mono

interface RegisterUserUseCase {
    fun register(registerRequest: RegisterRequest): Mono<RegisterResult>
}