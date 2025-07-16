package com.ethyllium.authservice.application.util

import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

private val logger = LoggerFactory.getLogger("FireAndForget")

fun Mono<*>.fireAndForget() {
    this.subscribeOn(Schedulers.boundedElastic()) // Run on a background thread
        .doOnError { err -> logger.error("Fire-and-forget operation failed", err) }.subscribe( // Fire and forget!
            null, // On success, do nothing
            { err -> /* Error is already handled by doOnError, but we need the lambda */ })
}