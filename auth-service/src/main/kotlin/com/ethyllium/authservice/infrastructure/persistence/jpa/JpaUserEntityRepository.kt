package com.ethyllium.authservice.infrastructure.persistence.jpa

import com.ethyllium.authservice.application.service.LoginAttempt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaUserEntityRepository : JpaRepository<UserEntity, String> {
    fun findByEmail(email: String): MutableList<UserEntity>

    @Query("select * from users where _username=:userName", nativeQuery = true)
    fun findByUsername(userName: String): MutableList<UserEntity>
    fun existsUserEntityBy_username(userName: String): Boolean

    @Modifying
    @Query("UPDATE users SET enabled=true WHERE _username=:userId", nativeQuery = true)
    fun enableUser(userId: String)
    fun existsUserByEmail(email: String): Boolean

    @Modifying
    @Query("UPDATE users SET totp=:secret WHERE _username=:userId", nativeQuery = true)
    fun setUserSecret(userId: String, secret: String)

    @Modifying
    @Query("UPDATE users SET is_account_locked=false WHERE _username=:userId", nativeQuery = true)
    fun unblockUser(userId: String)

    @Query(
        " SELECT u, la FROM users u LEFT JOIN login_attempt_entity la ON u._username = la.username WHERE u.email = :email",
        nativeQuery = true
    )
    fun findUserAndAttemptByEmail(@Param("email") email: String): Pair<UserEntity, LoginAttempt>?

    @Modifying
    @Query("UPDATE users SET email_verified_at=NOW() WHERE _username=:username", nativeQuery = true)
    fun verifyEmailNow(username: String)
}