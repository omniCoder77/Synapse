package com.ethyllium.authservice.domain.port.driven

import java.util.concurrent.TimeUnit

interface CacheRepository {
    fun store(key: String, data: Any, ttl: Long, unit: TimeUnit)
    fun store(key: String, data: Any)
    fun remove(key: String): Boolean
    fun get(key: String): Any?
}