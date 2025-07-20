package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.backup

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

@Service
class ReactiveBackupService(
    private val databaseConfig: DatabaseConfig, @Value("\${backup.directory}") private val backupDirectory: String
) {

    fun createBackup(): Mono<String> = Mono.fromCallable {
        val timestamp = Instant.now().toString().replace(":", "-")
        val backupFileName = "backup-$timestamp.dump"
        val backupFile = Paths.get(backupDirectory, backupFileName).toFile()

        Files.createDirectories(Paths.get(backupDirectory))

        val processBuilder = ProcessBuilder(
            "/usr/bin/pg_dump",
            "-h",
            databaseConfig.host,
            "-p",
            databaseConfig.port.toString(),
            "-U",
            databaseConfig.username,
            "-Fc",
            "-f",
            backupFile.absolutePath,
            databaseConfig.databaseName
        )

        val process = processBuilder.start()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("Backup failed with exit code: $exitCode")
        }

        backupFile.absolutePath
    }.subscribeOn(Schedulers.boundedElastic())
}