package com.ethyllium.productservice.application.config

import com.ethyllium.productservice.infrastructure.adapter.outbound.security.AuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
class ReactiveAuthorizationFilter(
    private val authorizationEvaluatorService: AuthorizationInterceptor
) : WebFilter {

    private val excludedPaths = listOf("/health", "/actuator", "/swagger-ui", "/v3/api-docs")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        if (excludedPaths.any { path.startsWith(it) }) return chain.filter(exchange)

        return authorizationEvaluatorService.isAuthorized(exchange).flatMap { isAuthorized ->
            if (isAuthorized) {
                chain.filter(exchange)
            } else {
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.setComplete()
            }
        }.onErrorResume { e ->
            println("Authorization error: ${e.message}")
            exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            exchange.response.setComplete()
        }
    }
}
