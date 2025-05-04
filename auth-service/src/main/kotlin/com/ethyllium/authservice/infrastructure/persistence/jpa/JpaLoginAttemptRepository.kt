package com.ethyllium.authservice.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaLoginAttemptRepository : JpaRepository<LoginAttemptEntity, String> {
    fun findLoginAttemptByUsername(username: String): MutableList<LoginAttemptEntity>

    @Modifying
    @Query("update login_attempt_entity set attempt=0 where username=:username", nativeQuery = true)
    fun resetAttempt(username: String)

    @Modifying
    @Query(
        "insert into login_attempt_entity_device_fingerprint(login_attempt_entity_username, device_fingerprint) values (:username, :deviceFingerprint)",
        nativeQuery = true
    )
    fun addDeviceFingerprint(username: String, deviceFingerprint: String)
}