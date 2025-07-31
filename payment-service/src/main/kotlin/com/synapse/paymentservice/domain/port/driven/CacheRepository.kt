package com.synapse.paymentservice.domain.port.driven

import reactor.core.publisher.Mono
import java.time.Duration
import java.time.temporal.TemporalUnit

interface CacheRepository {
    fun <T> get(key: String, clazz: Class<T>): Mono<T>
    fun set(key: String, value: Any, ttl: Long, ttlUnit: TemporalUnit): Mono<Void>
    fun delete(key: String): Mono<Boolean>
    fun hasKey(key: String): Mono<Boolean>
    fun getExpire(key: String): Mono<Duration>
    fun setExpire(key: String, ttl: Long, ttlUnit: TemporalUnit): Mono<Boolean>
}