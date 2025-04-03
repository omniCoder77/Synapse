package com.ethyllium.gatewayservice.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class AuthFilter(
    private val webClientBuilder: WebClient.Builder
) : GatewayFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (token == null) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        return authenticate(token).flatMap { authResponse ->
                val mutatedRequest = exchange.request.mutate().header("X-User-Id", authResponse.userId).build()
                chain.filter(exchange.mutate().request(mutatedRequest).build())
            }.onErrorResume {
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.setComplete()
            }
    }

    private fun authenticate(token: String): Mono<AuthResponse> {
        return webClientBuilder.build().post().uri("lb://auth-service/auth/authenticate")
            .header(HttpHeaders.AUTHORIZATION, token).retrieve().bodyToMono(AuthResponse::class.java)
    }
}

data class AuthResponse(val userId: String)