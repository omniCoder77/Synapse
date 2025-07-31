package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.application.util.VerificationConstants
import com.ethyllium.productservice.domain.exception.SellerNotFoundException
import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.*
import com.ethyllium.productservice.domain.port.driver.SellerService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.RegisterSellerRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.security.SecureRandom
import java.time.Duration
import java.util.*

@Service
class SellerServiceImpl(
    private val sellerRepository: SellerRepository,
    private val outboxEntityRepository: OutboxEntityRepository,
    private val otpService: OtpService,
    private val emailService: EmailService,
    private val cacheRepository: CacheRepository,
    @Value("\${expiration.email-minutes}") private val emailExpirationMinutes: Int,
    private val tempSellerRepository: TempSellerRepository,
) : SellerService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
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
        ).flatMap { updated ->
            if (updated) {
                outboxEntityRepository.publishSellerUpdated(
                    sellerId,
                    businessName,
                    displayName,
                    address,
                    businessInfo,
                    sellerRating,
                    policies,
                    bankDetails,
                    taxInfo
                ).thenReturn(true)
            } else {
                Mono.just(false)
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
                logger.error("Failed to send OTP for seller: $sellerId", e)
                false
            }
        }
    }

    @Transactional
    override fun updatePhoneNumber(sellerId: String, code: String, phoneNumber: String): Mono<Boolean> {
        if (otpService.verifyOtp(phoneNumber, code)) {
            return sellerRepository.updatePhoneNumber(sellerId, phoneNumber = phoneNumber)
        }
        logger.warn("Failed to verify OTP for phone number update for seller: $sellerId")
        return Mono.just(false)
    }

    override fun initiateEmailVerification(sellerId: String, email: String) {
        val randomBytes = ByteArray(24)
        SecureRandom().nextBytes(randomBytes)
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)

        Mono.fromRunnable<Unit> { emailService.sendVerificationEmail(email, token, emailExpirationMinutes) }
            .subscribeOn(Schedulers.boundedElastic()).then(
                cacheRepository.put(
                    token, mapOf(
                        VerificationConstants.EMAIL to email, VerificationConstants.USER_ID to sellerId
                    ), Duration.ofMinutes(emailExpirationMinutes.toLong())
                )
            ).doOnError { ex ->
                logger.error("Failed to cache verification token for user {}: {}", sellerId, ex.message)
            }.subscribe()
    }

    @Transactional
    override fun updateEmail(token: String): Mono<Boolean> {
        return cacheRepository.get<Map<String, Any>>(token).flatMap {
            if (it == null) return@flatMap Mono.error(IllegalArgumentException("Token has expired or is invalid"))

            val userId = it[VerificationConstants.USER_ID] as String
            val email = it[VerificationConstants.EMAIL] as String

            sellerRepository.updateEmail(userId, email = email).flatMap { updated ->
                if (updated) {
                    cacheRepository.remove(token).thenReturn(true)
                } else {
                    Mono.just(false)
                }
            }
        }
    }

    @Transactional
    override fun registerSeller(
        registerSeller: RegisterSellerRequest, sellerId: String
    ): Mono<Seller> {
        return tempSellerRepository.findBySellerId(sellerId).flatMap { tempSeller ->
            val seller = Seller(
                id = sellerId,
                displayName = tempSeller.name,
                email = tempSeller.email,
                phone = tempSeller.phoneNumber,
                businessName = registerSeller.businessName,
                address = registerSeller.address,
                businessInfo = registerSeller.businessInfo,
                sellerRating = registerSeller.sellerRating,
                policies = registerSeller.policies,
                bankDetails = registerSeller.bankDetails,
                taxInfo = registerSeller.taxInfo,
                status = SellerStatus.valueOf(registerSeller.status.uppercase()),
                verificationStatus = VerificationStatus.valueOf(registerSeller.verificationStatus.uppercase())
            )
            sellerRepository.insert(seller).flatMap { savedSeller ->
                tempSellerRepository.delete(sellerId).then(outboxEntityRepository.publishSellerCreated(savedSeller))
                    .thenReturn(savedSeller)
            }
        }.switchIfEmpty(Mono.error(SellerNotFoundException("Temporary seller data not found for registration.")))
    }

    @Transactional
    override fun delete(sellerId: String): Mono<Boolean> {
        return sellerRepository.delete(sellerId).flatMap { deleted ->
            if (deleted) {
                outboxEntityRepository.publishSellerDeleted(sellerId).thenReturn(true)
            } else {
                Mono.just(false)
            }
        }
    }
}