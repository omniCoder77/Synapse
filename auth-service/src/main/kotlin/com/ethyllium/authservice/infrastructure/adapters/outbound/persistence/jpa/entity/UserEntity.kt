package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity

import com.ethyllium.authservice.domain.model.Role
import com.ethyllium.authservice.domain.model.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

@Table("users")
data class UserEntity(
    @Id val username: UUID = UUID.randomUUID(),
    @Column("password") val _password: String = "",
    @Column("email") val email: String = "",
    @Column("roles") val roles: String = "", // Roles are stored as a comma-separated string
    @Column("is_account_locked") val isAccountLocked: Boolean = false,
    @Column("is_account_enabled") val isAccountEnabled: Boolean = false,
    val enabled: Boolean = false,
    @Column("mfa") val mfa: Boolean = false,
    @Column("phone_number") val phoneNumber: String = "",
    @Column("email_verified_at") val emailVerifiedAt: LocalDateTime? = null,
    @Column("phone_number_verified_at") val phoneNumberVerifiedAt: LocalDateTime? = null,
    val totp: String? = null,
    @Column("refresh_token") val refreshToken: String = "",
    @Version @Column("version") val version: Long = 0,
    @CreatedDate @Column("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate @Column("updated_at") val updatedAt: LocalDateTime = LocalDateTime.now(),
) : UserDetails {

    fun toUser(): User = User(
        username = username,
        password = _password,
        email = email,
        role = roles.split(",").map { Role.valueOf(it) }, // Roles are stored as a comma-separated string
        isAccountLocked = isAccountLocked,
        isEnabled = enabled,
        phoneNumber = phoneNumber,
        isMfaEnabled = mfa
    )

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        roles.split(",").map { SimpleGrantedAuthority(it) }.toMutableList()

    override fun isEnabled(): Boolean = isAccountEnabled

    override fun getPassword(): String = _password

    override fun getUsername(): String = username.toString()

    override fun isAccountNonLocked(): Boolean = !isAccountLocked
}

fun User.toUserEntity(refreshToken: String, mfaTotp: String?) = UserEntity(
    username = this.username,
    _password = this.password,
    email = this.email,
    roles = this.role.joinToString(), // Should be a comma-separated string if you support multiple roles later
    isAccountLocked = this.isAccountLocked,
    enabled = this.isEnabled,
    mfa = this.isMfaEnabled,
    phoneNumber = this.phoneNumber,
    totp = mfaTotp,
    refreshToken = refreshToken,
)