package com.ethyllium.productservice.infrastructure.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "sellers")
@CompoundIndexes(
    CompoundIndex(name = "status_verification_idx", def = "{'status': 1, 'verificationStatus': 1}")
)
data class SellerDocument(
    @Id val id: String,
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
)