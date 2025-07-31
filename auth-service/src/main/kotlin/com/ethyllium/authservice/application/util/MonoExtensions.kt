package com.ethyllium.authservice.application.util

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

fun <T>Mono<T?>.scheduleDb() {
    subscribeOn(Schedulers.boundedElastic()).subscribe()
}