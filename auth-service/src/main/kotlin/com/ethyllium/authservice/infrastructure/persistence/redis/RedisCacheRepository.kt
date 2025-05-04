package com.ethyllium.authservice.infrastructure.persistence.redis

import com.ethyllium.authservice.domain.port.driven.CacheRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisCacheRepository(private val redisTemplate: RedisTemplate<String, Any>) : CacheRepository {
    override fun store(key: String, data: Any, ttl: Long, unit: TimeUnit) {
        redisTemplate.opsForValue().set(key, data.toString(), ttl, unit)
    }

    override fun store(key: String, data: Any) {
        redisTemplate.opsForValue().set(key, data.toString())
    }

    override fun remove(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    override fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }
}