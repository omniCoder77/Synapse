package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.redis

import com.ethyllium.authservice.domain.port.driven.CacheRepository
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.temporal.TemporalUnit

@Component
class RedisCacheRepository(
    private val reactiveRedisOperations: ReactiveRedisOperations<String, Any>,
) : CacheRepository {

    override fun store(key: String, data: Any, ttl: Long, unit: TemporalUnit): Mono<Boolean> {
        return reactiveRedisOperations.opsForValue().set(key, data.toString(), Duration.of(ttl, unit))
    }

    override fun store(key: String, data: Any): Mono<Boolean> {
        return reactiveRedisOperations.opsForValue().set(key, data.toString())
    }

    override fun remove(key: String): Mono<Long> {
        return reactiveRedisOperations.delete(key)
    }

    override fun read(key: String): Mono<Any?> {
        return reactiveRedisOperations.opsForValue().get(key)
    }

    override fun storeHash(key: String, data: Map<String, Any>, ttl: Long, unit: TemporalUnit): Mono<Boolean> {
        return Mono.zip(
            reactiveRedisOperations.opsForHash<String, Any>().putAll(key, data),
            reactiveRedisOperations.expire(key, Duration.of(ttl, unit))
        ).map { it.t2 }
    }

    override fun readHash(key: String): Mono<Map<String, String>> {
        return reactiveRedisOperations.opsForHash<String, String>().entries(key).collectMap({ it.key }, { it.value })
            .filter { it.isNotEmpty() }
    }
}