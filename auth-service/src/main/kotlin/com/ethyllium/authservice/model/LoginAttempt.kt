package com.ethyllium.authservice.model

import jakarta.persistence.*
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
data class LoginAttempt(
    @Id
    val username: String = "",
    var lastLoginAttempt: Date = Date(System.currentTimeMillis()),
    @Column(columnDefinition = "SMALLINT") var attempt: Int = 0,
    @ElementCollection(fetch = FetchType.EAGER)
    val deviceFingerprint: MutableList<String> = mutableListOf(),
)