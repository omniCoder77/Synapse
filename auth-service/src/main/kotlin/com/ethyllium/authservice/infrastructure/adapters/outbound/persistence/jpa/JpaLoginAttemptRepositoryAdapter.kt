package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa

import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.LoginAttemptEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class JpaLoginAttemptRepositoryAdapter(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : LoginAttemptRepository {
    override fun save(loginAttempt: LoginAttemptEntity): Mono<LoginAttemptEntity> {
        return r2dbcEntityTemplate.insert(loginAttempt)
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
        INSERT INTO login_attempt (username, device_fingerprint) 
        VALUES ($1, ARRAY[$2])
        ON CONFLICT (username) 
        DO UPDATE SET device_fingerprint = array_append(login_attempt.device_fingerprint, $2)
        WHERE NOT ($2 = ANY(login_attempt.device_fingerprint))
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