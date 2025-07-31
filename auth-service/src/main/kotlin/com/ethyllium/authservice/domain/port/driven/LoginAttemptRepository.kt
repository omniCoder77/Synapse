package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.LoginAttemptEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface LoginAttemptRepository {
    fun save(loginAttempt: LoginAttemptEntity): Mono<LoginAttemptEntity>
    fun save(loginAttempt: List<LoginAttemptEntity>): Mono<Void>
    fun getFingerprints(username: UUID): Mono<MutableList<String>>
    fun addFingerprint(username: UUID, deviceFingerprint: String): Mono<Boolean>
    fun resetAttempt(username: UUID): Mono<Boolean>
}