package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.model.RegisterResult
import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.TotpSecretGenerator
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.domain.port.driver.RegisterUserUseCase
import com.ethyllium.authservice.domain.port.driver.UserEventPublisher
import com.ethyllium.authservice.domain.util.CredentialValidator
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.RegisterRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RegisterUseCaseImpl(
    private val userCreationService: UserCreationService,
    private val userEventPublisher: UserEventPublisher,
    private val tokenService: TokenService,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val qrCodeGenerator: QrCodeGenerator
) : RegisterUserUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val credentialValidator = CredentialValidator()

    override fun register(registerRequest: RegisterRequest): Mono<RegisterResult> {
        credentialValidator.validateCredential(registerRequest.email, registerRequest.password)?.let {
            return Mono.just(RegisterResult.Failure(it))
        }

        val user = registerRequest.toUser()
        val refreshToken = tokenService.generateRefreshToken(user.username.toString())
        val mfaTotp = if (registerRequest.mfa) totpSecretGenerator.generateTotpSecret(user.email) else null

        return userCreationService.createAndPersistUser(user, refreshToken, mfaTotp?.second).flatMap { addedUser ->
            val event = UserRegisteredEvent(
                userId = addedUser.username,
                email = addedUser.email,
                deviceFingerprint = registerRequest.deviceFingerprint
            )
            userEventPublisher.publish(event).thenReturn(addedUser)
        }.flatMap { addedUser ->
            if (mfaTotp != null) {
                val qrCode = qrCodeGenerator.generateQrCode(uri = mfaTotp.first)
                Mono.just(RegisterResult.MfaImage(qrCode))
            } else {
                val accessToken = tokenService.generateAccessToken(addedUser.username.toString())
                Mono.just(RegisterResult.Token(accessToken, refreshToken))
            }
        }.doOnError { error ->
            logger.error(
                "Critical registration path failed for {}", registerRequest.email, error
            )
        }
    }
}