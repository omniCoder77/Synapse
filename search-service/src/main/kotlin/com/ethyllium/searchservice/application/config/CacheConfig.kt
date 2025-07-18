package com.ethyllium.searchservice.application.config

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun myRedisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder ->
            builder.withCacheConfiguration(
                    "cache1", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10))
                ).withCacheConfiguration(
                    "cache2", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1))
                )
        }
    }
}