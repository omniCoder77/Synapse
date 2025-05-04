package com.ethyllium.authservice.infrastructure.persistence.jpa

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import java.util.Date

@Entity
data class LoginAttemptEntity(
    @Id
    val username: String = "",
    var lastLoginAttempt: Date = Date(System.currentTimeMillis()),
    @Column(columnDefinition = "SMALLINT") var attempt: Int = 0,
    @ElementCollection(fetch = FetchType.EAGER)
    val deviceFingerprint: MutableList<String> = mutableListOf(),
)