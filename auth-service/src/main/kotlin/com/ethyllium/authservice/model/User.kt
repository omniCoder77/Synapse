package com.ethyllium.authservice.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Table(name = "users", indexes = [Index(columnList = "_username", unique = true)])
@Entity(name = "users")
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    @Id val _username: String = Uuid.random().toString(),
    var _password: String = "",
    @Column(unique = true, updatable = false) val email: String = "",
    @ElementCollection(fetch = FetchType.EAGER) val roles: MutableList<String> = mutableListOf(),
    val isAccountLocked: Boolean = false,
    val enabled: Boolean = false,
    val mfa: Boolean = false,
    val phoneNumber: String = "",
    val emailVerifiedAt: LocalDateTime? = null,
    val phoneNumberVerifiedAt: LocalDateTime? = null,
    val totp: String? = null,
    var refreshToken: String = ""
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles.mapTo(mutableListOf()) { SimpleGrantedAuthority(it) }
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

    override fun getPassword(): String {
        return _password
    }

    override fun getUsername(): String {
        return _username
    }

    override fun isAccountNonLocked(): Boolean {
        return !isAccountLocked
    }
}
