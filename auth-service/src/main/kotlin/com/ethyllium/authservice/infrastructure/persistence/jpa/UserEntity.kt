package com.ethyllium.authservice.infrastructure.persistence.jpa

import com.ethyllium.authservice.domain.model.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Table(
    indexes = [Index(name = "idx_username", columnList = "_username", unique = true), Index(name = "idx_email", columnList = "email", unique = true)]
)
@Entity(name = "users")
data class UserEntity @OptIn(ExperimentalUuidApi::class) constructor(

    @NaturalId
    @Id val _username: String = Uuid.Companion.random().toString(),
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
    var refreshToken: String = "",
    @Version
    var version: Long = 0,
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    var updatedAt: LocalDateTime = createdAt,
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

fun User.toUserEntity(refreshToken: String, mfaTotp: String?) = UserEntity(
    _username = this.username,
    _password = this.password,
    email = this.email,
    roles = mutableListOf(this.role),
    isAccountLocked = this.isAccountLocked,
    enabled = this.isEnabled,
    mfa = this.isMfaEnabled,
    phoneNumber = this.phoneNumber,
    refreshToken = refreshToken,
    totp = mfaTotp
)