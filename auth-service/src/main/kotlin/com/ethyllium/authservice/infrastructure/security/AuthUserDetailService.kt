package com.ethyllium.authservice.infrastructure.security

import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class AuthUserDetailService(
    private val jpaUserEntityRepository: JpaUserEntityRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return jpaUserEntityRepository.findUserBy_username(username).firstOrNull() as UserDetails
    }
}