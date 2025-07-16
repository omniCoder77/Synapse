package com.ethyllium.authservice.application.config

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = redisConnectionFactory
        template.setEnableTransactionSupport(true)
        return template
    }

    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, Any> {
        val serializer: Jackson2JsonRedisSerializer<Any> = Jackson2JsonRedisSerializer(Any::class.java)

        val builder: RedisSerializationContext.RedisSerializationContextBuilder<String, Any> =
            RedisSerializationContext.newSerializationContext(StringRedisSerializer())

        val context: RedisSerializationContext<String?, Any> = builder.value(serializer).build()

        return ReactiveRedisTemplate<String, Any>(factory, context)
    }

    @Bean
    fun userRegisteredEventTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, UserRegisteredEvent> {
        val keySerializer = StringRedisSerializer.UTF_8

        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, UserRegisteredEvent::class.java)

        val builder = RedisSerializationContext.newSerializationContext<String, UserRegisteredEvent>(keySerializer)
        val context = builder.value(valueSerializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}