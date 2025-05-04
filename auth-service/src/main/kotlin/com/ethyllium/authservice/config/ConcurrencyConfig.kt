package com.ethyllium.authservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock

@Configuration
class ConcurrencyConfig {
    
    @Bean(name = ["registrationTaskExecutor"])
    fun registrationTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 20
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("reg-exec-")
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(60)
        executor.initialize()
        return executor
    }
    
    @Bean
    fun userRepositoryLockManager(): UserRepositoryLockManager {
        return UserRepositoryLockManager()
    }
}

class UserRepositoryLockManager {
    private val locks = ConcurrentHashMap<String, ReentrantLock>()
    
    fun getLock(username: String): ReentrantLock {
        return locks.computeIfAbsent(username) { ReentrantLock() }
    }
    
    fun releaseLock(username: String, lock: ReentrantLock) {
        if (lock.isHeldByCurrentThread) {
            lock.unlock()
        }
        locks.remove(username, lock)
    }
    
    @Scheduled(fixedRate = 300000) // 5 minutes
    fun cleanupExpiredLocks() {
        locks.entries.removeIf { (_, lock) -> !lock.isLocked }
    }
}