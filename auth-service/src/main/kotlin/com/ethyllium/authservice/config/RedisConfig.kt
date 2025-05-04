package com.ethyllium.authservice.config

import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.integration.redis.util.RedisLockRegistry
import java.time.Duration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int = 6379,
    @Value("\${spring.data.redis.password}") private val password: String,
    @Value("\${spring.data.redis.database}") private val database: Int = 0
) {

    constructor() : this("localhost", 6379, "", 0)

    // Existing single-node connection factory
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port)
        if (password.isNotEmpty()) {
            config.password = RedisPassword.of(password)
        }
        config.database = database

        val clientConfig =
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofMillis(500)).shutdownTimeout(Duration.ZERO)
                .build()

        return LettuceConnectionFactory(config, clientConfig)
    }

    // New: Redis Cluster Client (for advanced cluster operations)
    @Bean(destroyMethod = "shutdown")
    fun redisClusterClient(): RedisClusterClient {
        val redisUri =
            io.lettuce.core.RedisURI.Builder.redis(host, port).withPassword(password.ifEmpty { null }?.toCharArray())
                .withDatabase(database).build()

        return RedisClusterClient.create(listOf(redisUri))
    }

    // New: Cluster connection
    @Bean(destroyMethod = "close")
    fun clusterConnection(redisClusterClient: RedisClusterClient): StatefulRedisClusterConnection<String, String> {
        return redisClusterClient.connect()
    }

    // New: Async cluster commands
    @Bean
    fun clusterAsyncCommands(
        clusterConnection: StatefulRedisClusterConnection<String, String>
    ): RedisAdvancedClusterAsyncCommands<String, String> {
        return clusterConnection.async()
    }

    // Existing cache manager
    @Bean
    fun redisCacheManagerBuilder(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val defaultCacheConfig =
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)).disableCachingNullValues()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
                ).serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJackson2JsonRedisSerializer()
                    )
                )

        val cacheConfigurations = mutableMapOf<String, RedisCacheConfiguration>()
        cacheConfigurations["tokenCache"] = defaultCacheConfig.entryTtl(Duration.ofMinutes(5))
        cacheConfigurations["userCache"] = defaultCacheConfig.entryTtl(Duration.ofSeconds(30))
        cacheConfigurations["otpCache"] = defaultCacheConfig.entryTtl(Duration.ofMinutes(2))

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations).transactionAware().build()
    }

    // Existing templates
    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = redisConnectionFactory
        template.setEnableTransactionSupport(true)
        return template
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.setEnableTransactionSupport(true)
        return template
    }

    // Existing lock registry
    @Bean
    fun redisLockRegistry(redisConnectionFactory: RedisConnectionFactory): RedisLockRegistry {
        return RedisLockRegistry(redisConnectionFactory, "registration-locks", 30000)
    }
}