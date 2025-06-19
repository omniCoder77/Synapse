package com.ethyllium.authservice.infrastructure.persistence.jpa

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaLoginAttemptRepository : JpaRepository<LoginAttemptEntity, String> {

    @Modifying
    @Transactional
    @Query("UPDATE LoginAttemptEntity l SET l.attempt = 0, l.lastLoginAttempt = CURRENT_TIMESTAMP WHERE l.username = :username")
    fun resetAttempt(username: String)
}