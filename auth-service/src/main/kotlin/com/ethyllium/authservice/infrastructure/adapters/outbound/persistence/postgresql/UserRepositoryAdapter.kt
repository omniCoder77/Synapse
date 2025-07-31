package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.UserEntity
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.toUserEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Component
class UserRepositoryAdapter(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : UserRepository {
    override fun findByEmail(email: String): Mono<UserEntity> {
        return r2dbcEntityTemplate.select(Query.query(Criteria.where("email").`is`(email)), UserEntity::class.java)
            .singleOrEmpty()
    }

    override fun findUserByUsername(userName: UUID): Mono<User> {
        return r2dbcEntityTemplate.select(
            Query.query(Criteria.where("username").`is`(userName)), UserEntity::class.java
        ).singleOrEmpty().map { it.toUser() }
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

    override fun updatePassword(userIdAndPassword: List<Pair<UUID, String>>): Mono<Void> {
        if (userIdAndPassword.isEmpty()) return Mono.empty()

        val sql = StringBuilder("UPDATE users SET password = CASE username ")
        val bindings = mutableListOf<Any>()
        val usernameList = mutableListOf<String>()

        userIdAndPassword.forEachIndexed { index, user ->
            sql.append("WHEN $${index * 2 + 1} THEN $${index * 2 + 2} ")
            bindings.add(user.first)
            bindings.add(user.second)
            usernameList.add("'${user.first}'")
        }

        sql.append("END WHERE username IN (${usernameList.joinToString(", ")})")

        var spec = r2dbcEntityTemplate.databaseClient.sql(sql.toString())
        bindings.forEachIndexed { i, value ->
            spec = spec.bind(i, value)
        }

        return spec.then()
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