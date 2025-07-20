package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.backup

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.io.File

@Configuration
@EnableScheduling
class BackupScheduler(
    private val backupService: ReactiveBackupService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "\${backup.schedule.cron:0 0 2 * * ?}") // Default: 2 AM daily
    fun scheduleBackup() {
        backupService.createBackup().doOnSuccess { filePath ->
                logger.info("Backup successfully created at: $filePath")
                cleanupOldBackups(File(filePath).parentFile)
            }.doOnError { e -> logger.error("Backup failed", e) }.subscribe()
    }

    private fun cleanupOldBackups(directory: File) {
        val backupFiles = directory.listFiles { file ->
            file.name.startsWith("backup-") && file.name.endsWith(".dump")
        }?.sortedBy { it.lastModified() }

        // Keep last 7 backups
        backupFiles?.let {
            if (it.size > 7) {
                it.take(it.size - 7).forEach { oldBackup ->
                    oldBackup.delete()
                    logger.info("Deleted old backup: ${oldBackup.name}")
                }
            }
        }
    }
}