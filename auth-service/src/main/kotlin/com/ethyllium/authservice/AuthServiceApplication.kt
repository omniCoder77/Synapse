package com.ethyllium.authservice

import com.ethyllium.authservice.domain.model.Role
import com.ethyllium.authservice.infrastructure.adapters.outbound.jwt.JwtKeyManager
import com.ethyllium.authservice.infrastructure.adapters.outbound.jwt.JwtServiceImpl
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import java.util.*

@SpringBootApplication
@EnableDiscoveryClient
class AuthServiceApplication

//fun main(args: Array<String>) {
//    runApplication<AuthServiceApplication>(*args)
//}

fun main() {
    val userId = UUID.randomUUID().toString()
    val jwtKeyManager = JwtKeyManager(
        keyStoreFilePath = "/home/rishabh/Backend/Synapse/auth-service/keystore.jks",
        keyStorePasswordStr = "asfj3489urwejjfe4r",
        keyAlias = "jwtKey",
        keyPasswordStr = "328942urijdfkj()87"
    ).apply { init() }
    runBlocking { jwtKeyManager.getKey() }
    val jwtService = JwtServiceImpl(
        accessTokenExpiration = Long.MAX_VALUE, refreshTokenExpiration = 0L, jwtKeyManager = jwtKeyManager
    ).apply { init() }

    var token = jwtService.generateTestToken(
        subject = userId, additionalClaims = mapOf(
            "role" to Role.ADMIN.name,
        )
    )

    println("Generated JWT Token Admin:")
    println(token)

    token = jwtService.generateTestToken(
        subject = userId, additionalClaims = mapOf(
            "role" to Role.SELLER.name,
        )
    )

    println("Generated JWT Token Seller:")
    println(token)

    token = jwtService.generateTestToken(
        subject = userId, additionalClaims = mapOf(
            "role" to Role.CUSTOMER.name,
        )
    )

    println("Generated JWT Token Customer:")
    println(token)
    println("UserId $userId")
}