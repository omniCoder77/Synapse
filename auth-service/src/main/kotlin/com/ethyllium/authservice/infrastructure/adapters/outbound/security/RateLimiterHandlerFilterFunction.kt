package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisCallback
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalTime
import java.util.*


@Component
class RateLimiterHandlerFilterFunction(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Long>,
    @Value("\${MAX_REQUESTS_PER_MINUTE}") private val MAX_REQUESTS_PER_MINUTE: Long
) : HandlerFilterFunction<ServerResponse, ServerResponse> {
    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse?>): Mono<ServerResponse?> {
        val currentMinute = LocalTime.now().minute
        val key = String.format("rl_%s:%s", requestAddress(request.remoteAddress()), currentMinute)

        return reactiveRedisTemplate
            .opsForValue().get(key)
            .flatMap(
                { value ->
                    if (value >= MAX_REQUESTS_PER_MINUTE)
                        ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build() else
                        incrAndExpireKey(key, request, next)
                }
            ).switchIfEmpty(incrAndExpireKey(key, request, next))
    }

    private fun incrAndExpireKey(
        key: String, request: ServerRequest, next: HandlerFunction<ServerResponse?>
    ): Mono<ServerResponse?> {
        return reactiveRedisTemplate.execute(ReactiveRedisCallback<MutableList<Any?>?> { connection ->
            val bbKey: ByteBuffer = ByteBuffer.wrap(key.toByteArray())
            Mono.zip(
                connection.numberCommands().incr(bbKey), connection.keyCommands().expire(bbKey, Duration.ofSeconds(59L))
            ).then(Mono.empty())
        }).then(next.handle(request))
    }

    private fun requestAddress(maybeAddress: Optional<InetSocketAddress>): String? {
        return if (maybeAddress.isPresent) maybeAddress.get().hostName else ""
    }
}