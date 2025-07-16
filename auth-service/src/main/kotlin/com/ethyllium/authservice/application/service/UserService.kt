package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.port.driven.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun updateUserSecret(userId: UUID, secret: String): Mono<Long> {
        return userRepository.setUserSecret(userId, secret)
    }
}