package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.application.util.Claims
import com.ethyllium.authservice.domain.model.RegisterResult
import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.TotpSecretGenerator
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.domain.port.driver.RegisterUserUseCase
import com.ethyllium.authservice.domain.port.driver.UserEventPublisher
import com.ethyllium.authservice.domain.util.CredentialValidator
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.RegisterRequest
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Component
class RegisterUseCaseImpl(
    private val userCreationService: UserCreationService,
    private val userEventPublisher: UserEventPublisher,
    private val tokenService: TokenService,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val qrCodeGenerator: QrCodeGenerator,
    private val cpuScheduler: Scheduler
) : RegisterUserUseCase {

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
                deviceFingerprint = registerRequest.deviceFingerprint,
                password = registerRequest.password
            )
            userEventPublisher.publish(event).subscribeOn(Schedulers.boundedElastic()).subscribe()
            (if (mfaTotp != null) {
                Mono.fromCallable { qrCodeGenerator.generateQrCode(mfaTotp.first) }.subscribeOn(cpuScheduler)
            } else {
                Mono.fromCallable { tokenService.generateAccessToken(addedUser.username.toString(), mapOf(Claims.USER_ID to addedUser.username,
                    Claims.ROLE to addedUser.roles)) }
                    .subscribeOn(cpuScheduler)
            }).map {
                if (mfaTotp != null) RegisterResult.MfaImage(it as ByteArray)
                else RegisterResult.Token(it as String, refreshToken)
            }
        }
    }
}