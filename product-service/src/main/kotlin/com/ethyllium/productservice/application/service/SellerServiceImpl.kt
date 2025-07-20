package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.application.util.VerificationConstants
import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.*
import com.ethyllium.productservice.domain.port.driver.SellerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.security.SecureRandom
import java.time.Duration
import java.util.*

@Service
class SellerServiceImpl(
    private val sellerRepository: SellerRepository,
    private val eventPublisher: EventPublisher,
    private val otpService: OtpService,
    private val emailService: EmailService,
    private val cacheRepository: CacheRepository,
    @Value("\${expiration.email-minutes}") private val emailExpirationMinutes: Int,
) : SellerService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun create(seller: Seller): Mono<Seller> {
        return sellerRepository.insert(seller).doOnSuccess { sel ->
            Mono.just(eventPublisher.publishSellerCreated(sel)).subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    override fun update(
        sellerId: String,
        businessName: String?,
        displayName: String?,
        address: Address?,
        businessInfo: BusinessInfo?,
        sellerRating: SellerRating?,
        policies: SellerPolicies?,
        bankDetails: BankDetails?,
        taxInfo: TaxInfo?,
        status: SellerStatus?,
    ): Mono<Boolean> {
        return sellerRepository.update(
            sellerId,
            businessName,
            displayName,
            address,
            businessInfo,
            sellerRating,
            policies,
            bankDetails,
            taxInfo,
            status
        ).doOnSuccess { updated ->
            if (updated) {
                Mono.just(
                    eventPublisher.publishSellerUpdated(
                        sellerId,
                        businessName,
                        displayName,
                        address,
                        businessInfo,
                        sellerRating,
                        policies,
                        bankDetails,
                        taxInfo
                    )
                ).subscribeOn(Schedulers.boundedElastic()).subscribe()
            }
        }
    }

    override fun getById(sellerId: String): Mono<Seller> {
        return sellerRepository.findById(sellerId)
    }

    override fun initiatePhoneVerification(sellerId: String, phoneNumber: String): Mono<Boolean> {
        return sellerRepository.findById(sellerId).map {
            try {
                otpService.sendOtp(phoneNumber)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun updatePhoneNumber(sellerId: String, code: String, phoneNumber: String): Mono<Boolean> {
        return if (otpService.verifyOtp(phoneNumber, code)) sellerRepository.updatePhoneNumber(
            sellerId, phoneNumber = phoneNumber
        ) else {
            logger.warn("Failed to verify OTP for phone number update for seller: $sellerId")
            Mono.just(false)
        }
    }

    override fun initiateEmailVerification(sellerId: String, email: String) {
        val randomBytes = ByteArray(24) // 24 bytes = 32 URL-safe Base64 chars
        SecureRandom().nextBytes(randomBytes)
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
        Mono.zip(
            Mono.fromRunnable<Void> { (emailService.sendVerificationEmail(email, token, emailExpirationMinutes)) },
            cacheRepository.put(
                token.toString(), mapOf(
                    VerificationConstants.EMAIL to email, VerificationConstants.USER_ID to sellerId
                ), Duration.ofMinutes(emailExpirationMinutes.toLong())
            ).doOnError { ex ->
                logger.error(
                    "Failed to cache verification token for user {}: {}",
                    sellerId,
                    ex.message
                )
            }).subscribeOn(Schedulers.boundedElastic()).subscribe()
    }

    override fun updateEmail(token: String): Mono<Boolean> {
        return cacheRepository.get<Map<String, Any>>(token).flatMap {
            if (it == null) Mono.error(IllegalArgumentException("Token has expired or is invalid"))
            else {
                val userId = it[VerificationConstants.USER_ID] as String
                val email = it[VerificationConstants.EMAIL] as String
                Mono.zip(
                    sellerRepository.updateEmail(userId, email = email), cacheRepository.remove(token)
                ).map { it.t1 && it.t2 }
            }
        }
    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return sellerRepository.delete(sellerId).doOnSuccess { deleted ->
            if (deleted) {
                Mono.just(eventPublisher.publishSellerDeleted(sellerId)).subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
        }
    }
}