package com.ethyllium.authservice.infrastructure.persistence.jpa

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.domain.port.driven.UserRepository
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(private val jpaUserEntityRepository: JpaUserEntityRepository) : UserRepository {
    override fun findByEmail(email: String): UserEntity? {
        return jpaUserEntityRepository.findByEmail(email).firstOrNull()
    }

    override fun findUserByUsername(userName: String): UserEntity? {
        return jpaUserEntityRepository.findByUsername(userName).firstOrNull()
    }

    override fun enableUser(userId: String) {
        jpaUserEntityRepository.enableUser(userId)
    }

    override fun existsByUsername(username: String): Boolean {
        return jpaUserEntityRepository.existsUserEntityBy_username(username)
    }

    override fun existsUserByEmail(email: String): Boolean {
        return jpaUserEntityRepository.existsUserByEmail(email)
    }

    override fun setUserSecret(userId: String, secret: String) {
        jpaUserEntityRepository.setUserSecret(userId, secret)
    }

    override fun unblockUser(userId: String) {
        jpaUserEntityRepository.unblockUser(userId)
    }

    override fun addUser(user: User, refreshToken: String, mfaTotp: String?): UserEntity {
        return jpaUserEntityRepository.save(user.toUserEntity(refreshToken, mfaTotp))
    }
}