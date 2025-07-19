package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("login_attempts")
data class LoginAttemptEntity(
    @Id val username: UUID,
    @Column("last_login_attempt") var lastLoginAttempt: Instant = Instant.now(),
    @Column(value = "attempt") var attempt: Int = 0,
    @Column("device_fingerprint") val deviceFingerprint: MutableList<String> = mutableListOf(),
)