package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("login_attempts")
data class LoginAttemptEntity(
    @Id val username: UUID = UUID.randomUUID(),
    @Column("last_login_attempt") var lastLoginAttempt: Date = Date(System.currentTimeMillis()),
    @Column(value = "attempt") var attempt: Int = 0,
    @Column("device_fingerprint") val deviceFingerprint: MutableList<String> = mutableListOf(),
)