package com.ethyllium.authservice.infrastructure.persistence.jpa

import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class JpaLoginAttemptRepositoryAdapter(private val jpaLoginAttemptRepository: JpaLoginAttemptRepository) :
    LoginAttemptRepository {
    override fun save(loginAttempt: LoginAttemptEntity) {
        jpaLoginAttemptRepository.save(loginAttempt)
    }

    override fun getFingerprints(username: String): MutableList<String> {
        val attempts = jpaLoginAttemptRepository.findById(username).getOrNull()
            ?: throw UsernameNotFoundException("Username $username not found")
        return attempts.deviceFingerprint
    }

    override fun addFingerprint(username: String, deviceFingerprint: String) {
        val entity = jpaLoginAttemptRepository.findById(username).getOrNull() ?: LoginAttemptEntity(username = username)
        entity.deviceFingerprint.add(deviceFingerprint)
        save(entity)
    }

    override fun resetAttempt(username: String) {
        jpaLoginAttemptRepository.resetAttempt(username)
    }
}