package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql

import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.LoginAttemptEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class JpaLoginAttemptRepositoryAdapter(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : LoginAttemptRepository {
    override fun save(loginAttempt: LoginAttemptEntity): Mono<LoginAttemptEntity> {
        return r2dbcEntityTemplate.insert(loginAttempt)
    }

    override fun save(loginAttempt: List<LoginAttemptEntity>): Mono<Void> {
        if (loginAttempt.isEmpty()) return Mono.empty()

        val sql =
            StringBuilder("INSERT INTO login_attempts (username, last_login_attempt, attempt, device_fingerprint) VALUES ")
        val bindings = mutableListOf<Any>()

        loginAttempt.forEachIndexed { index, entity ->
            sql.append("($${index * 4 + 1}, $${index * 4 + 2}, $${index * 4 + 3}, $${index * 4 + 4}),")
            bindings.add(entity.username)
            bindings.add(entity.lastLoginAttempt)
            bindings.add(entity.attempt)
            bindings.add(entity.deviceFingerprint.toTypedArray())
        }
        sql.setLength(sql.length - 1)
        var spec = r2dbcEntityTemplate.databaseClient.sql(sql.toString())
        bindings.forEachIndexed { i, value ->
            spec = spec.bind(i, value)
        }

        return spec.then()
    }

    override fun getFingerprints(username: UUID): Mono<MutableList<String>> {
        return r2dbcEntityTemplate.select(
            Query.query(Criteria.where(LoginAttemptEntity::username.name).`is`(username)),
            LoginAttemptEntity::class.java
        ).singleOrEmpty().flatMap {
            Mono.just(it.deviceFingerprint)
        }
    }

    override fun addFingerprint(username: UUID, deviceFingerprint: String): Mono<Boolean> {
        val sql = """
        INSERT INTO login_attempts (username, device_fingerprint) 
        VALUES ($1, ARRAY[$2])
        ON CONFLICT (username) 
        DO UPDATE SET device_fingerprint = array_append(login_attempts.device_fingerprint, $2)
        WHERE NOT ($2 = ANY(login_attempts.device_fingerprint))
    """

        return r2dbcEntityTemplate.databaseClient.sql(sql).bind(0, username).bind(1, deviceFingerprint).fetch()
            .rowsUpdated().map { it > 0 }
    }

    override fun resetAttempt(username: UUID): Mono<Boolean> {
        return r2dbcEntityTemplate.update(
            Query.query(Criteria.where(LoginAttemptEntity::username.name).`is`(username)),
            Update.update(LoginAttemptEntity::attempt.name, 0).set(LoginAttemptEntity::lastLoginAttempt.name, null),
            LoginAttemptEntity::class.java
        ).map { it > 0 }
    }
}