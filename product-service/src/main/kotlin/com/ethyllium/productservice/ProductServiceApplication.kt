package com.ethyllium.productservice

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.backup.DatabaseConfig
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.backup.ReactiveBackupService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(DatabaseConfig::class)
@EnableDiscoveryClient
class ProductServiceApplication {
    @Bean
    fun reactiveBackupService(
        databaseConfig: DatabaseConfig, @Value("\${backup.directory}") backupDirectory: String
    ): ReactiveBackupService {
        return ReactiveBackupService(databaseConfig, backupDirectory)
    }
}

fun main(args: Array<String>) {
    runApplication<ProductServiceApplication>(*args)
}
