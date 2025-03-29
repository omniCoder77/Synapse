package com.ethyllium.authservice.repository

import com.ethyllium.authservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): MutableList<User>

    @Modifying
    @Query("UPDATE users SET isAccountLocked=true WHERE _username=:userId")
    fun blockUser(userId: String)

    fun findUserBy_username(userName: String): MutableList<User>

    @Modifying
    @Query("UPDATE users SET enabled=true WHERE _username=:userId")
    fun enableUser(userId: String)
    fun existsUserByEmail(email: String): Boolean

    @Modifying
    @Query("UPDATE users SET totp=:secret WHERE _username=:userId")
    fun setUserSecret(userId: String, secret: String)

    @Modifying
    @Query("UPDATE users SET isAccountLocked=false WHERE _username=:userId")
    fun unblockUser(userId: String)
}