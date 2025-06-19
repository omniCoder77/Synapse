package com.ethyllium.productservice.config

import org.ehcache.jsr107.EhcacheCachingProvider
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.jcache.JCacheCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.Caching

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): JCacheCacheManager {
        val cachingProvider = Caching.getCachingProvider(EhcacheCachingProvider::class.java.name)
        val cacheManager = cachingProvider.getCacheManager(
            javaClass.getResource("/ehcache.xml")?.toURI(), javaClass.classLoader
        )
        return JCacheCacheManager(cacheManager)
    }
}
