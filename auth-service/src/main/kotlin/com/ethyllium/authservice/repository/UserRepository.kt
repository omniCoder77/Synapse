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
    @Query("UPDATE users SET isAccountLocked=true WHERE userId=:userId")
    fun blockUser(userId: String)
    fun findUserByUserName(userName: String): MutableList<User>

    @Modifying
    @Query("UPDATE users SET enabled=true WHERE userId=:userId")
    fun enableUser(userId: String)
    fun existsUserByEmail(email: String): Boolean

    @Modifying
    @Query("UPDATE users SET totp=:secret WHERE userId=:userId")
    fun setUserSecret(userId: String, secret: String)

    @Modifying
    @Query("UPDATE users SET isAccountLocked=false WHERE userId=:userId")
    fun unblockUser(userId: String)
}