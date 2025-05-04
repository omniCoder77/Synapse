package com.ethyllium.authservice.domain.service

import java.util.concurrent.CompletionStage

interface LockService {
    fun lockAcquired(key: String, value: String, ttl: Long): CompletionStage<Boolean?>
    fun releaseLock(key: String, value: String): CompletionStage<Boolean?>
}