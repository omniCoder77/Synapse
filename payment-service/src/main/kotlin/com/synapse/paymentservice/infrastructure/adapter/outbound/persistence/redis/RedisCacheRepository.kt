package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.synapse.paymentservice.domain.port.driven.CacheRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.temporal.TemporalUnit

@Repository
class RedisCacheRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) : CacheRepository {

    override fun <T> get(key: String, clazz: Class<T>): Mono<T> {
        return reactiveRedisTemplate.opsForValue().get(key).map { objectMapper.convertValue(it, clazz) }
    }

    override fun set(key: String, value: Any, ttl: Long, ttlUnit: TemporalUnit): Mono<Void> {
        return reactiveRedisTemplate.opsForValue().set(key, value, Duration.of(ttl, ttlUnit)).then()
    }

    override fun delete(key: String): Mono<Boolean> {
        return reactiveRedisTemplate.opsForValue().delete(key)
    }

    override fun hasKey(key: String): Mono<Boolean> {
        return reactiveRedisTemplate.hasKey(key)
    }

    override fun getExpire(key: String): Mono<Duration> {
        return reactiveRedisTemplate.getExpire(key)
    }

    override fun setExpire(key: String, ttl: Long, ttlUnit: TemporalUnit): Mono<Boolean> {
        return reactiveRedisTemplate.expire(key, Duration.of(ttl, ttlUnit))
    }
}