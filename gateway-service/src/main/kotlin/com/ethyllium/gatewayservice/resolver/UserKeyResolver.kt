package com.ethyllium.gatewayservice.resolver

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component("userKeyResolver")
class UserKeyResolver(
    private val webClientBuilder: WebClient.Builder
) : KeyResolver {
    override fun resolve(exchange: ServerWebExchange): Mono<String> {
        val userId = exchange.request.headers.getFirst("X-User-Id")
        if (userId != null) {
            return getUserInfo(userId)
                .flatMap { userInfo ->
                    if (!userInfo.isPremium) Mono.just(userId) else Mono.empty()
                }
                .switchIfEmpty(Mono.empty())
        }
        return Mono.empty()
    }

    private fun getUserInfo(userId: String): Mono<UserInfo> {
        return webClientBuilder.build()
            .get()
            .uri("lb://user-service/user/userInfo")
            .header("X-User-Id", userId)
            .retrieve()
            .bodyToMono(UserInfo::class.java)
    }
}

data class UserInfo(val isPremium: Boolean)