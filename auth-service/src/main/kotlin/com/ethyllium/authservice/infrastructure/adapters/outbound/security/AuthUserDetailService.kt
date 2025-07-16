package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import com.ethyllium.authservice.domain.port.driven.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthUserDetailService(
    private val userRepository: UserRepository
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return try {
            val uuid = UUID.fromString(username)
            userRepository.findUserByUsername(uuid).cast(UserDetails::class.java)
                .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found with username: $username")))
        } catch (e: IllegalArgumentException) {
            Mono.error(UsernameNotFoundException("Invalid username format (expected UUID): $username"))
        }
    }
}