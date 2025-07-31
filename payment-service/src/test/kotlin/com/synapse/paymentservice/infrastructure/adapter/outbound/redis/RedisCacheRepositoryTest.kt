package com.synapse.paymentservice.infrastructure.adapter.outbound.redis

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.redis.RedisCacheRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisCacheRepositoryTest {

    @Autowired
    private lateinit var redisConnectionFactory: ReactiveRedisConnectionFactory

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>
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

    @Test
    fun `hasKey should return true for existing key`() {
        val key = "existing_key"
        val value = "existing_value"

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.hasKey(key)).expectNext(true).verifyComplete()
    }

    @Test
    fun `hasKey should return false for non-existent key`() {
        StepVerifier.create(redisCacheRepository.hasKey("nonexistent_key")).expectNext(false).verifyComplete()
    }

    @Test
    fun `getExpire should return duration for key with TTL`() {
        val key = "key_with_ttl"
        val value = "value_with_ttl"
        val ttl = 10L

        StepVerifier.create(redisCacheRepository.set(key, value, ttl, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.getExpire(key)).assertNext { duration ->
            assertTrue(duration.seconds > 0 && duration.seconds <= ttl)
        }.verifyComplete()
    }

    @Test
    fun `getExpire should return empty for non-existent key`() {
        StepVerifier.create(redisCacheRepository.getExpire("nonexistent_key")).verifyComplete()
    }

    @Test
    fun `setExpire should update TTL for existing key`() {
        val key = "key_to_update_ttl"
        val value = "value_to_update_ttl"
        val initialTtl = 60L
        val newTtl = 120L

        StepVerifier.create(redisCacheRepository.set(key, value, initialTtl, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.setExpire(key, newTtl, ChronoUnit.SECONDS)).expectNext(true)
            .verifyComplete()

        StepVerifier.create(redisCacheRepository.getExpire(key)).assertNext { duration ->
            assertTrue(duration.seconds > initialTtl && duration.seconds <= newTtl)
        }.verifyComplete()
    }

    @Test
    fun `setExpire should return false for non-existent key`() {
        StepVerifier.create(redisCacheRepository.setExpire("nonexistent_key", 10, ChronoUnit.SECONDS)).expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `should handle concurrent operations correctly`() {
        val key = "concurrent_key"
        val value1 = "value1"
        val value2 = "value2"

        val set1 = redisCacheRepository.set(key, value1, 10, ChronoUnit.SECONDS)
        val set2 = redisCacheRepository.set(key, value2, 10, ChronoUnit.SECONDS)

        StepVerifier.create(Mono.zip(set1, set2)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).expectNext(value2).verifyComplete()
    }

    @Test
    fun `should handle large values correctly`() {
        val key = "large_value_key"
        val largeValue = "a".repeat(1_000_000)

        StepVerifier.create(redisCacheRepository.set(key, largeValue, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).expectNext(largeValue).verifyComplete()
    }

    @Test
    fun `should handle special characters in keys and values`() {
        val key = "special@key#with\$chars%"
        val value = "value\nwith\t\r\bspecial\u0000chars"

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, String::class.java)).expectNext(value).verifyComplete()
    }

    @Test
    fun `should emit error when type casting fails`() {
        val key = "type_mismatch_key"
        val value = "string_value"

        StepVerifier.create(redisCacheRepository.set(key, value, 10, ChronoUnit.SECONDS)).verifyComplete()

        StepVerifier.create(redisCacheRepository.get(key, Int::class.java)).expectError(IllegalArgumentException::class.java)
            .verify()
    }
}