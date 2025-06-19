package com.ethyllium.authservice.domain.port.driven

import java.util.concurrent.TimeUnit

interface CacheRepository {
    fun store(key: String, data: Any, ttl: Long, unit: TimeUnit)
    fun store(key: String, data: Any)
    fun remove(key: String): Boolean
    fun read(key: String): Any?
    fun readHash(key: String): Map<String, String>?
    fun storeHash(key: String, data: Map<String, String>, ttl: Long, unit: TimeUnit)
}