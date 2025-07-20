package com.ethyllium.productservice.application.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfig {
    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Long> {
        val jdkSerializationRedisSerializer = JdkSerializationRedisSerializer()
        val stringRedisSerializer = StringRedisSerializer.UTF_8
        val longToStringSerializer = GenericToStringSerializer(Long::class.java)
        val template = ReactiveRedisTemplate(
            factory,
            RedisSerializationContext.newSerializationContext<String, Long>(jdkSerializationRedisSerializer)
                .key(stringRedisSerializer).value(longToStringSerializer).build()
        )
        return template
    }

    @Bean
    fun script(): RedisScript<Boolean> {
        return RedisScript.of(ClassPathResource("scripts/rateLimiter.lua"), Boolean::class.java)
    }

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerKotlinModule()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            registerModule(JavaTimeModule())
        }
    }
}