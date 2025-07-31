package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.redis

import com.ethyllium.productservice.domain.port.driven.CuckooFilter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import redis.clients.jedis.UnifiedJedis

@Component
class RedisCuckooFilter(private val jedis: UnifiedJedis) : CuckooFilter {
    override fun exists(key: String, item: String): Mono<Boolean> {
        return Mono.just(jedis.cfExists(key, item))
    }

    override fun exists(key: String, vararg item: String): Mono<List<Boolean>> {
        return Mono.just(jedis.cfMExists(key, *item))
    }

    override fun add(table: String, entity: String): Mono<Boolean> {
        return Mono.just(jedis.cfAdd(table, entity))
    }

    override fun remove(table: String, entity: String): Mono<Boolean> {
        return Mono.just(jedis.cfDel(table, entity))
    }

    override fun add(key: String, vararg items: String): Mono<List<Boolean>> {
        return Mono.just(jedis.cfInsert(key, *items))
    }
}