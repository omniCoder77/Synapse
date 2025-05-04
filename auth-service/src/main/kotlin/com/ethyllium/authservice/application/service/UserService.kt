package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val jpaUserEntityRepository: JpaUserEntityRepository) {

    @Transactional
    fun updateUserSecret(userId: String, secret: String) {
        jpaUserEntityRepository.setUserSecret(userId, secret)
    }
}