package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.controller

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.controller.RateLimiterHandlerFilterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.http.MediaType.TEXT_PLAIN

import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.BodyInserters


@Bean
fun routes(rateLimiterHandlerFilterFunction : RateLimiterHandlerFilterFunction): RouterFunction<ServerResponse?> {
    return route() //
        .GET("/api/v1/product", { r ->
            ok() //
                .contentType(TEXT_PLAIN) //
                .body(BodyInserters.fromValue("PONG"))
        } //
        ).filter(rateLimiterHandlerFilterFunction).build()
}