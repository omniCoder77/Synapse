package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.productservice.domain.model.AccountType
import com.ethyllium.productservice.domain.model.BusinessType
import com.ethyllium.productservice.domain.model.Coordinates
import com.ethyllium.productservice.domain.model.SellerBadge

data class SellerUpdatedEvent(
    val sellerId: String,
    val businessName: String?,
    val displayName: String?,
    val street: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val coordinates: Coordinates?,
    val businessType: BusinessType?,
    val registrationNumber: String?,
    val taxId: String?,
    val website: String?,
    val description: String?,
    val yearEstablished: Int?,
    val employeeCount: Int?,
    val averageRating: Long,
    val totalRatings: Int,
    val ratingDistribution: Map<Int, Int>,
    val badges: List<SellerBadge>,
    val returnPolicy: String?,
    val shippingPolicy: String?,
    val privacyPolicy: String?,
    val termsOfService: String?,
    val warrantyPolicy: String?,
    val accountHolderName: String?,
    val accountNumber: String?,
    val routingNumber: String?,
    val bankName: String?,
    val accountType: AccountType?,
    val vatNumber: String?,
    val taxExempt: Boolean?,
    val taxJurisdictions: List<String>?
) : Event