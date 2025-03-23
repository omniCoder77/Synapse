package com.ethyllium.authservice.service

import com.ethyllium.authservice.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun updateUserSecret(userId: String, secret: String) {
        userRepository.setUserSecret(userId, secret)
    }
}