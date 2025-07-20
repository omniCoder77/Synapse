package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.backup

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "database")
data class DatabaseConfig(
    val host: String,
    val port: Int,
    val username: String,
    val databaseName: String
)