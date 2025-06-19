package com.ethyllium.authservice.domain.model

import java.time.Duration

interface DistributedLockService {
    fun acquireLock(lockKey: String, lockValue: String, expiration: Duration): Boolean
    fun releaseLock(lockKey: String, lockValue: String): Boolean
}