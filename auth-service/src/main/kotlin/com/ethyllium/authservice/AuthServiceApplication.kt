package com.ethyllium.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
// eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIlNFTExFUiJdLCJzdWIiOiJ1c2VyLWlkIiwiaWF0IjoxNzUyOTM5MjQyfQ.noLBJ-CNLhry9Kd6hwzfeXQerAboGX_9G-lL4Qw_kuM