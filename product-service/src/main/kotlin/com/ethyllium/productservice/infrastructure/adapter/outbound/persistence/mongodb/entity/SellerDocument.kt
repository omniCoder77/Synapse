package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity

import com.ethyllium.productservice.domain.model.AccountType
import com.ethyllium.productservice.domain.model.Address
import com.ethyllium.productservice.domain.model.BadgeType
import com.ethyllium.productservice.domain.model.BankDetails
import com.ethyllium.productservice.domain.model.BusinessInfo
import com.ethyllium.productservice.domain.model.BusinessType
import com.ethyllium.productservice.domain.model.Coordinates
import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.model.SellerBadge
import com.ethyllium.productservice.domain.model.SellerPolicies
import com.ethyllium.productservice.domain.model.SellerRating
import com.ethyllium.productservice.domain.model.SellerStatus
import com.ethyllium.productservice.domain.model.TaxInfo
import com.ethyllium.productservice.domain.model.VerificationStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneOffset

@Document(collection = "sellers")
@CompoundIndexes(
    CompoundIndex(name = "status_verification_idx", def = "{'status': 1, 'verificationStatus': 1}")
)
data class SellerDocument(
    @Id val sellerId: String,
    @TextIndexed val businessName: String,
    val displayName: String,
    @Indexed(unique = true) val email: String,
    val phone: String? = null,
    val address: AddressDocument,
    val businessInfo: BusinessInfoDocument,
    val sellerRating: SellerRatingDocument,
    val policies: SellerPoliciesDocument,
    val bankDetails: BankDetailsDocument? = null,
    val taxInfo: TaxInfoDocument,
    @Indexed val status: String,
    @Indexed val verificationStatus: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Seller = Seller(
        id = this.sellerId,
        businessName = this.businessName,
        displayName = this.displayName,
        email = this.email,
        phone = this.phone,
        address = this.address.toDomain(),
        businessInfo = this.businessInfo.toDomain(),
        sellerRating = this.sellerRating.toDomain(),
        policies = this.policies.toDomain(),
        bankDetails = this.bankDetails?.toDomain(),
        taxInfo = this.taxInfo.toDomain(),
        status = SellerStatus.valueOf(this.status),
        verificationStatus = VerificationStatus.valueOf(this.verificationStatus)
    )
}

fun Seller.toDocument(): SellerDocument = SellerDocument(
    sellerId = this.id,
    businessName = this.businessName,
    displayName = this.displayName,
    email = this.email,
    phone = this.phone,
    address = this.address.toDocument(),
    businessInfo = this.businessInfo.toDocument(),
    sellerRating = this.sellerRating.toDocument(),
    policies = this.policies.toDocument(),
    bankDetails = this.bankDetails?.toDocument(),
    taxInfo = this.taxInfo.toDocument(),
    status = this.status.name,
    verificationStatus = this.verificationStatus.name
)

fun AddressDocument.toDomain(): Address = Address(
    street = street,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    coordinates = coordinates?.toDomain()
)

fun Address.toDocument(): AddressDocument = AddressDocument(
    street = street,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    coordinates = coordinates?.toDocument()
)

fun CoordinatesDocument.toDomain(): Coordinates = Coordinates(
    latitude = latitude, longitude = longitude
)

fun Coordinates.toDocument(): CoordinatesDocument = CoordinatesDocument(
    latitude = latitude, longitude = longitude
)

fun BusinessInfoDocument.toDomain(): BusinessInfo = BusinessInfo(
    businessType = BusinessType.valueOf(businessType),
    registrationNumber = registrationNumber,
    taxId = taxId,
    website = website,
    description = description,
    yearEstablished = yearEstablished,
    employeeCount = employeeCount
)

fun BusinessInfo.toDocument(): BusinessInfoDocument = BusinessInfoDocument(
    businessType = businessType.name,
    registrationNumber = registrationNumber,
    taxId = taxId,
    website = website,
    description = description,
    yearEstablished = yearEstablished,
    employeeCount = employeeCount
)

fun SellerRatingDocument.toDomain(): SellerRating = SellerRating(
    averageRating = averageRating,
    totalRatings = totalRatings,
    ratingDistribution = ratingDistribution,
    badges = badges.map { it.toDomain() })

fun SellerRating.toDocument(): SellerRatingDocument = SellerRatingDocument(
    averageRating = averageRating,
    totalRatings = totalRatings,
    ratingDistribution = ratingDistribution,
    badges = badges.map { it.toDocument() })

fun SellerBadgeDocument.toDomain(): SellerBadge = SellerBadge(
    type = BadgeType.valueOf(type),
    name = name,
    description = description,
    earnedAt = LocalDateTime.ofInstant(earnedAt, ZoneOffset.UTC)
)

fun SellerBadge.toDocument(): SellerBadgeDocument = SellerBadgeDocument(
    type = type.name, name = name, description = description, earnedAt = this.earnedAt.toInstant(ZoneOffset.UTC)
)

fun SellerPoliciesDocument.toDomain(): SellerPolicies = SellerPolicies(
    returnPolicy = returnPolicy,
    shippingPolicy = shippingPolicy,
    privacyPolicy = privacyPolicy,
    termsOfService = termsOfService,
    warrantyPolicy = warrantyPolicy
)

fun SellerPolicies.toDocument(): SellerPoliciesDocument = SellerPoliciesDocument(
    returnPolicy = returnPolicy,
    shippingPolicy = shippingPolicy,
    privacyPolicy = privacyPolicy,
    termsOfService = termsOfService,
    warrantyPolicy = warrantyPolicy
)

fun BankDetailsDocument.toDomain(): BankDetails = BankDetails(
    accountHolderName = accountHolderName,
    accountNumber = accountNumber,
    routingNumber = routingNumber,
    bankName = bankName,
    accountType = AccountType.valueOf(accountType)
)

fun BankDetails.toDocument(): BankDetailsDocument = BankDetailsDocument(
    accountHolderName = accountHolderName,
    accountNumber = accountNumber,
    routingNumber = routingNumber,
    bankName = bankName,
    accountType = accountType.name
)

fun TaxInfoDocument.toDomain(): TaxInfo = TaxInfo(
    taxId = taxId, vatNumber = vatNumber, taxExempt = taxExempt, taxJurisdictions = taxJurisdictions
)

fun TaxInfo.toDocument(): TaxInfoDocument = TaxInfoDocument(
    taxId = taxId, vatNumber = vatNumber, taxExempt = taxExempt, taxJurisdictions = taxJurisdictions
)