package com.ethyllium.authservice.repository

import com.ethyllium.authservice.model.LoginAttempt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LoginAttemptRepository: JpaRepository<LoginAttempt, String> {
    fun findLoginAttemptByUsername(username: String): MutableList<LoginAttempt>

    @Modifying
    @Query("update LoginAttempt set attempt=0 where username=:username")
    fun resetAttempt(username: String)

    @Modifying
    @Query("insert into login_attempt_device_fingerprint(login_attempt_username, device_fingerprint) values (:username, :deviceFingerprint)", nativeQuery = true)
    fun addDeviceFingerprint(username: String, deviceFingerprint: String)
}