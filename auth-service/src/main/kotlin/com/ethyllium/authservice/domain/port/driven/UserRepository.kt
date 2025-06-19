package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.infrastructure.persistence.jpa.UserEntity

interface UserRepository {
    fun findByEmail(email: String): UserEntity?
    fun findUserByUsername(userName: String): UserEntity?
    fun enableUser(userId: String)
    fun existsUserByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun setUserSecret(userId: String, secret: String)
    fun unblockUser(userId: String)
    fun addUser(user: User, refreshToken: String, mfaTotp: String?): UserEntity
}