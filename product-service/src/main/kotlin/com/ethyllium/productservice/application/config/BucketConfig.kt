package com.ethyllium.productservice.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class BucketConfig {
    @Bean
    fun createBucket(): Bucket {
        val bucket =
            Bucket.builder().addLimit(Bandwidth.builder().capacity(3).refillGreedy(3, Duration.ofMinutes(10)).build())
                .build()
        return bucket
    }
}