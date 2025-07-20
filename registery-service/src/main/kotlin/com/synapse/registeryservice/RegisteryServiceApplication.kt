package com.synapse.registeryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class RegisteryServiceApplication

fun main(args: Array<String>) {
    runApplication<RegisteryServiceApplication>(*args)
}
