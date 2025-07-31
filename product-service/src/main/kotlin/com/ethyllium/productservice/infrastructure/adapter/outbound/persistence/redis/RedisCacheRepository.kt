package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.redis

import com.ethyllium.productservice.domain.port.driven.CacheRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class RedisCacheRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
) : CacheRepository {

    companion object {
        const val DEFAULT_TTL_SECONDS = 3600L // 1 hour default TTL
    }

    override fun <T> get(key: String): Mono<T?> {
        return reactiveRedisTemplate.opsForValue().get(key).map { jsonString ->
            val wrapper = objectMapper.readValue(jsonString, CacheWrapper::class.java)
            objectMapper.readValue(wrapper.data, object : TypeReference<T>() {})
        }
    }

    override fun <T> put(key: String, value: T, ttl: Duration): Mono<Boolean> {
        return try {
            val wrapper = CacheWrapper(
                data = objectMapper.writeValueAsString(value),
                className = value!!::class.java.name,
                timestamp = System.currentTimeMillis()
            )
            val jsonString = objectMapper.writeValueAsString(wrapper)

            reactiveRedisTemplate.opsForValue().set(key, jsonString, ttl)
        } catch (e: Exception) {
            Mono.error(CacheException("Failed to put value in cache for key: $key", e))
        }
    }

    override fun remove(key: String): Mono<Boolean> {
        return reactiveRedisTemplate.delete(key).map { it > 0 }.onErrorReturn(false)
    }

    override fun clear(): Mono<String> {
        return reactiveRedisTemplate.connectionFactory.reactiveConnection.serverCommands().flushAll()
    }
}

data class CacheWrapper(
    val data: String, val className: String, val timestamp: Long
)

class CacheException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)