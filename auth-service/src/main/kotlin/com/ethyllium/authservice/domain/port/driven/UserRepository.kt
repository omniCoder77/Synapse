package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.UserEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): Mono<UserEntity>
    fun findUserByUsername(userName: UUID): Mono<UserEntity>
    fun addUser(user: User, refreshToken: String, mfaTotp: String?): Mono<UserEntity>
    fun updatePassword(username: UUID, encodedPassword: String): Mono<Long>
    fun setUserSecret(username: UUID, secret: String): Mono<Long>
    fun enableUser(username: UUID): Mono<Long>
    fun emailVerifiedNow(username: UUID): Mono<Long>
}