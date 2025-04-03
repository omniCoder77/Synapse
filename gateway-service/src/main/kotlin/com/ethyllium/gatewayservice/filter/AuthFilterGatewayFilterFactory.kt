package com.ethyllium.gatewayservice.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class AuthFilterGatewayFilterFactory(
    private val webClientBuilder: WebClient.Builder
) : AbstractGatewayFilterFactory<Any>(Any::class.java) {

    override fun apply(config: Any): GatewayFilter {
        return AuthFilter(webClientBuilder)
    }
}