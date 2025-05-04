package com.ethyllium.authservice.config

import com.ethyllium.authservice.domain.model.DistributedLockService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import java.time.Duration

@Configuration
class RedisLockConfiguration {

    @Bean
    fun redisScript(): RedisScript<Boolean> {
        val script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
        """.trimIndent()
        return RedisScript.of(script, Boolean::class.java)
    }
    
    @Bean
    fun distributedLockService(
        stringRedisTemplate: StringRedisTemplate,
        redisScript: RedisScript<Boolean>
    ): DistributedLockService {
        return RedisDistributedLockService(stringRedisTemplate, redisScript)
    }
}

class RedisDistributedLockService(
    private val redisTemplate: StringRedisTemplate,
    private val redisScript: RedisScript<Boolean>
) : DistributedLockService {

    override fun acquireLock(lockKey: String, lockValue: String, expiration: Duration): Boolean {
        return redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, expiration) ?: false
    }

    override fun releaseLock(lockKey: String, lockValue: String): Boolean {
        return redisTemplate.execute(
            redisScript,
            listOf(lockKey),
            lockValue
        )
    }
}
