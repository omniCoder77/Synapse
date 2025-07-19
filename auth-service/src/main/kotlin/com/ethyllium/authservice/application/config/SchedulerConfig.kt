package com.ethyllium.authservice.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Configuration
class SchedulerConfig {
    @Bean
    fun cpuScheduler(): Scheduler {
        return Schedulers.newParallel("cpu-pool", Runtime.getRuntime().availableProcessors() * 5)
    }
}