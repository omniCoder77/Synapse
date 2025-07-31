package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.UserEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): Mono<UserEntity>
    fun findUserByUsername(userName: UUID): Mono<User>
    fun addUser(user: User, refreshToken: String, mfaTotp: String?): Mono<UserEntity>
    fun updatePassword(username: UUID, encodedPassword: String): Mono<Long>
    fun updatePassword(userIdAndPassword: List<Pair<UUID, String>>): Mono<Void>
    fun setUserSecret(username: UUID, secret: String): Mono<Long>
    fun enableUser(username: UUID): Mono<Long>
    fun emailVerifiedNow(username: UUID): Mono<Long>
}