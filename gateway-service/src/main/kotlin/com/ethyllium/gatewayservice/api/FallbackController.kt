package com.ethyllium.gatewayservice.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FallbackController {
    @GetMapping("/fallback/auth")
    fun authFallback(): String = "Auth service is temporarily unavailable"

    @GetMapping("/fallback/user")
    fun userFallback(): String = "User service is temporarily unavailable"

    @GetMapping("/fallback/other")
    fun otherFallback(): String = "Other service is temporarily unavailable"
}