package com.ethyllium.authservice.infrastructure.persistence.jpa

import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import org.springframework.stereotype.Component

@Component
class JpaLoginAttemptRepositoryAdapter(private val jpaLoginAttemptRepository: JpaLoginAttemptRepository) : LoginAttemptRepository {
    override fun save(loginAttempt: LoginAttemptEntity) {
        jpaLoginAttemptRepository.save(loginAttempt)
    }
}