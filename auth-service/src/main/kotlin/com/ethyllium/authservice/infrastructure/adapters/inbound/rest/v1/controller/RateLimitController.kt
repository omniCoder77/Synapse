package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.infrastructure.adapters.outbound.security.RateLimiterHandlerFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok


@Component
class RateLimitController(
    private val rateLimiterHandlerFilterFunction: RateLimiterHandlerFilterFunction
) {
    @Bean
    fun routes(): RouterFunction<ServerResponse?> {
        return route()
            .GET("/api/v1/auth", { r ->
                ok() //
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(BodyInserters.fromValue("PONG"))
            }
            ).filter(rateLimiterHandlerFilterFunction).build()
    }
}