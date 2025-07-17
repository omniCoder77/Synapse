package com.ethyllium.productservice.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "app.security")
@Component
data class SecurityProperties(
    var endpoints: Map<String, EndpointSecurity> = emptyMap(),
    var publicPaths: List<String> = listOf(
        "/actuator/health",
        "/public/**",
        "/swagger**",
        "/v3/api-docs**"
    ),
    var cacheEnabled: Boolean = true,
    var logAuthorizationAttempts: Boolean = true
)

data class EndpointSecurity(
    var methods: List<String> = emptyList(),
    var roles: List<String> = emptyList(),
    var allowAnonymous: Boolean = false,
    var description: String = ""
)