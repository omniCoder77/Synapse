package com.ethyllium.authservice.infrastructure.security

import com.ethyllium.authservice.domain.port.driven.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class AuthUserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        return userRepository.findUserByUsername(username) as UserDetails
    }
}