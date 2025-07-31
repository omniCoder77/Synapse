package com.ethyllium.productservice.domain.port.driven

import reactor.core.publisher.Mono

interface CuckooFilter {
    fun exists(key: String, item: String): Mono<Boolean>
    fun add(table: String, entity: String): Mono<Boolean>
    fun remove(table: String, entity: String): Mono<Boolean>
    fun add(key: String, vararg items: String): Mono<List<Boolean>>
    fun exists(key: String, vararg item: String): Mono<List<Boolean>>
}