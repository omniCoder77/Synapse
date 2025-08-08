package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import reactor.test.StepVerifier
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisCacheRepositoryTest {

    @Autowired
    private lateinit var redisConnectionFactory: ReactiveRedisConnectionFactory

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisOperations<String, Any>
    private lateinit var redisCacheRepository: RedisCacheRepository

    @BeforeAll
    fun setup() {
        redisCacheRepository = RedisCacheRepository(reactiveRedisTemplate, jacksonObjectMapper())

        reactiveRedisTemplate.execute { it.serverCommands().flushAll() }.blockLast()
    }

    @AfterAll
    fun tearDown() {
        redisConnectionFactory.reactiveConnection.serverCommands().flushAll().block()
        (redisConnectionFactory as LettuceConnectionFactory).destroy()
    }

    @Test
    fun `get should return empty Mono when key does not exist`() {
        StepVerifier.create(redisCacheRepository.get("nonexistent_key", String::class.java)).verifyComplete()
    }

    @Test
    fun `set and get should work for String value`() {
        val key = "test_string"
        val value = "test_value"

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).expectNext(value).verifyComplete()
    }

    @Test
    fun `set and get should work for Boolean value`() {
        val key = "test_bool"
        val value = true

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, Boolean::class.java)).expectNext(value).verifyComplete()
    }

    @Test
    fun `set and get should work for Int value`() {
        val key = "test_int"
        val value = 42

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, Int::class.java)).expectNext(value).verifyComplete()
    }

    @Test
    fun `set should expire key after TTL`() {
        val key = "expiring_key"
        val value = "expiring_value"
        val ttl = 1L // 1 second

        StepVerifier.create(redisCacheRepository.set(key, value, ttl, ChronoUnit.SECONDS)).verifyComplete()

        // Verify value exists before TTL
        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).expectNext(value).verifyComplete()

        // Wait for TTL to expire
        TimeUnit.SECONDS.sleep(ttl + 1)

        // Verify value is gone after TTL
        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).verifyComplete()
    }

    @Test
    fun `delete should remove existing key`() {
        val key = "key_to_delete"
        val value = "value_to_delete"

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.delete(key)).expectNext(true).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).verifyComplete()
    }

    @Test
    fun `delete should return false for non-existent key`() {
        StepVerifier.create(redisCacheRepository.delete("nonexistent_key")).expectNext(false).verifyComplete()
    }

}