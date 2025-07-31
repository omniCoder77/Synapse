package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.redis.RedisCacheRepository.Companion.DEFAULT_TTL_SECONDS
import reactor.core.publisher.Mono
import java.time.Duration

interface CacheRepository {
    fun <T> get(key: String): Mono<T?>

    fun <T> put(key: String, value: T,ttl: Duration = Duration.ofSeconds(DEFAULT_TTL_SECONDS)): Mono<Boolean>

    fun remove(key: String): Mono<Boolean>

    fun clear(): Mono<String>
}