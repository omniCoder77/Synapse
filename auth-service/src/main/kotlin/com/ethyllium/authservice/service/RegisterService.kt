package com.ethyllium.authservice.service

import com.ethyllium.authservice.exception.EmailExistsException
import com.ethyllium.authservice.exception.InvalidCredentialsException
import com.ethyllium.authservice.model.LoginAttempt
import com.ethyllium.authservice.model.User
import com.ethyllium.authservice.ports.OtpGenerator
import com.ethyllium.authservice.ports.SendMail
import com.ethyllium.authservice.ports.SendOtp
import com.ethyllium.authservice.ports.TokenGenerator
import com.ethyllium.authservice.repository.LoginAttemptRepository
import com.ethyllium.authservice.repository.UserRepository
import com.ethyllium.authservice.validation.ValidateUserCredentials
import org.postgresql.util.PSQLException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val validationService: ValidationService,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val jwtService: JwtService,
    private val sendMail: SendMail,
    private val tokenGenerator: TokenGenerator,
    private val otpGenerator: OtpGenerator,
    private val sendOtp: SendOtp
) {

    fun register(user: User, deviceFingerprint: String): String {
        val emailValidity = ValidateUserCredentials.validateEmail(user.email)
        val passwordValidity = ValidateUserCredentials.validatePassword(user.password)
        try {
            if (emailValidity != null) throw InvalidCredentialsException(emailValidity)
            if (passwordValidity != null) throw InvalidCredentialsException(passwordValidity)
            if (userRepository.existsUserByEmail(user.email)) throw EmailExistsException(user.email)
            user._password = passwordEncoder.encode(user._password)
            val refreshToken = jwtService.generateRefreshToken(user.username)
            user.refreshToken = refreshToken
            val accessToken = jwtService.generateAccessToken(user.username)
            val savedUser = userRepository.save(user)
            val verificationToken = tokenGenerator.generateToken(userId = user.username)
            sendMail.sendVerificationEmail(to = savedUser.email, verificationToken = verificationToken)
            val otp = otpGenerator.generateOtp(userId = user.username)
            // sendOtp.sendOtp(otp = otp, userId = user.username, phoneNumber = savedUser.phoneNumber)
            val loginAttempt = LoginAttempt(username = savedUser.username, deviceFingerprint = mutableListOf(deviceFingerprint))
            loginAttemptRepository.save(loginAttempt)
            return accessToken
        } catch (e: PSQLException) {
            if (e.sqlState == "23505") throw IllegalStateException("User already registered with given credentials")
            else throw e
        }
    }
}