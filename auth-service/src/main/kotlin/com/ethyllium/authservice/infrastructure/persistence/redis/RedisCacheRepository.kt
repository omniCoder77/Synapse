package com.ethyllium.authservice.infrastructure.persistence.redis

import com.ethyllium.authservice.domain.port.driven.CacheRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class RedisCacheRepository(private val redisTemplate: RedisTemplate<String, Any>) : CacheRepository {
    private val executor: Executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun store(key: String, data: Any, ttl: Long, unit: TimeUnit) {
        redisTemplate.opsForValue().set(key, data.toString(), ttl, unit)
    }

    override fun store(key: String, data: Any) {
        redisTemplate.opsForValue().set(key, data.toString())
    }

    override fun remove(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    override fun read(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun storeHash(key: String, data: Map<String, String>, ttl: Long, unit: TimeUnit) {
        redisTemplate.opsForHash<String, String>().putAll(key, data)
        redisTemplate.expire(key, ttl, unit)
    }

    override fun readHash(key: String): Map<String, String>? {
        val entries = redisTemplate.opsForHash<String, String>().entries(key)
        return entries.ifEmpty { null }
    }
}