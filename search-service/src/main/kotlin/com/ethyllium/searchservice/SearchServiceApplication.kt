package com.ethyllium.searchservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
class SearchServiceApplication

fun main(args: Array<String>) {
    runApplication<SearchServiceApplication>(*args)
}
