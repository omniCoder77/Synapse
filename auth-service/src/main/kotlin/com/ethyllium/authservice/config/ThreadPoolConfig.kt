package com.ethyllium.authservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class ThreadPoolConfig {

    @Bean(name = ["asyncTaskExecutor"])
    fun asyncTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 20      // Default threads
        executor.maxPoolSize = 100      // Max under load
        executor.queueCapacity = 500    // Queue buffer
        executor.setThreadNamePrefix("Async-")
        executor.initialize()
        return executor
    }
}