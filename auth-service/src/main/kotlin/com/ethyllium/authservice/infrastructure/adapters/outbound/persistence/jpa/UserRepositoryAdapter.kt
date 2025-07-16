package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.UserEntity
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.toUserEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

@Component
class UserRepositoryAdapter(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : UserRepository {
    override fun findByEmail(email: String): Mono<UserEntity> {
        return r2dbcEntityTemplate.select(Query.query(Criteria.where("email").`is`(email)), UserEntity::class.java)
            .singleOrEmpty()
    }

    override fun findUserByUsername(userName: UUID): Mono<UserEntity> {
        return r2dbcEntityTemplate.select(
            Query.query(Criteria.where("username").`is`(userName)), UserEntity::class.java
        ).singleOrEmpty().switchIfEmpty(Mono.error(IllegalArgumentException("User with username $userName not found")))
    }

    override fun addUser(user: User, refreshToken: String, mfaTotp: String?): Mono<UserEntity> {
        val userEntity = user.toUserEntity(refreshToken, mfaTotp)
        return r2dbcEntityTemplate.insert(userEntity)
    }

    override fun updatePassword(username: UUID, encodedPassword: String): Mono<Long> {
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("username").`is`(username)),
            Update.update("password", encodedPassword),
            UserEntity::class.java
        )
    }

    override fun setUserSecret(username: UUID, secret: String): Mono<Long> {
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("username").`is`(username)),
            Update.update(UserEntity::totp.name, secret),
            UserEntity::class.java
        )
    }

    override fun enableUser(username: UUID): Mono<Long> {
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("username").`is`(username)),
            Update.update(UserEntity::enabled.name, true),
            UserEntity::class.java
        )

    }

    override fun emailVerifiedNow(username: UUID): Mono<Long> {
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where("username").`is`(username)),
            Update.update(UserEntity::emailVerifiedAt.name, Instant.now()),
            UserEntity::class.java
        )

    }
}