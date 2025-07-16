package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.model.User
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class UserCreationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * This method contains the ONLY part of the registration that needs to be in a single database transaction.
     * It encodes the password and creates the user.
     */
    @Transactional
    fun createAndPersistUser(user: User, refreshToken: String, mfaTotp: String?): Mono<UserEntity> {
        // Password encoding is CPU-intensive, move it to a background thread pool.
        return Mono.fromCallable { passwordEncoder.encode(user.password) }
            .subscribeOn(Schedulers.parallel())
            .flatMap { encodedPassword ->
                user.password = encodedPassword // Update the model with the hashed password
                userRepository.addUser(user, refreshToken, mfaTotp)
            }
    }
}