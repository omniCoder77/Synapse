package com.ethyllium.authservice.domain.port.driven

import reactor.core.publisher.Mono
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

interface CacheRepository {
    fun store(key: String, data: Any, ttl: Long, unit: TemporalUnit): Mono<Boolean>
    fun store(key: String, data: Any): Mono<Boolean>
    fun remove(key: String): Mono<Long>
    fun read(key: String): Mono<Any?>
    fun readHash(key: String): Mono<Map<String, String>>
    fun storeHash(key: String, data: Map<String, Any>, ttl: Long, unit: TemporalUnit): Mono<Boolean>
}