package com.ethyllium.authservice.service

import com.ethyllium.authservice.ports.SendMail
import com.ethyllium.authservice.ports.TokenGenerator
import com.ethyllium.authservice.repository.LoginAttemptRepository
import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.util.Claims
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val jwtService: JwtService,
    private val tokenGenerator: TokenGenerator,
    private val sendMail: SendMail,
) {

    fun login(email: String, password: String, deviceFingerprint: String): LoginAttempt {
        val user = userRepository.findByEmail(email).firstOrNull() ?: throw UsernameNotFoundException(email)
        if (passwordEncoder.matches(password, user.password)) {
            val loginAttempt = loginAttemptRepository.findLoginAttemptByUsername(user.username)
                .firstOrNull()!!
            if (!loginAttempt.deviceFingerprint.contains(deviceFingerprint)) {
                if (user.mfa) {
                    val mfaToken = jwtService.generateAccessToken(
                        user.username, additionalClaims = mapOf(MfaPurpose.NEW_DEVICE_LOGIN.name to Claims.MFA_ACTION)
                    )
                    return LoginAttempt.MFALogin(mfaToken)
                } else {
                    val token = tokenGenerator.generateToken(userId = user.username)
                    sendMail.sendVerificationEmail(to = user.email, verificationToken = token)
                    return LoginAttempt.CredentialVerification
                }
            } else {
                if (user.mfa) {
                    val mfaToken = jwtService.generateAccessToken(
                        user.username, additionalClaims = mapOf(MfaPurpose.LOGIN.name to "mfa_purpose")
                    )
                    return LoginAttempt.MFALogin(mfaToken)
                } else {
                    val accessToken = jwtService.generateAccessToken(user.username)
                    loginAttemptRepository.resetAttempt(loginAttempt.username)
                    return LoginAttempt.Success(accessToken)
                }
            }
        }
        return LoginAttempt.InvalidCredentials
    }
}

sealed interface LoginAttempt {
    data class NewDeviceLogin(val token: String) : LoginAttempt
    data class MFALogin(val token: String) : LoginAttempt
    data object InvalidCredentials : LoginAttempt
    data object CredentialVerification : LoginAttempt
    data class Success(val token: String) : LoginAttempt
}