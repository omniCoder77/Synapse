package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto

data class AuthenticateResponse(val userId: String, val role: String)