package com.ethyllium.productservice.domain.entity

import java.time.LocalDateTime
import java.util.*

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val shortDescription: String? = null,
    val sku: String,
    val barcode: String? = null,
    val brand: Brand,
    val category: Category,
    val seller: Seller,
    val pricing: ProductPricing,
    val inventory: ProductInventory,
    val specifications: ProductSpecifications,
    val media: ProductMedia,
    val seo: ProductSEO,
    val shipping: ProductShipping,
    val reviews: ProductReviews,
    val variantCode: String = UUID.randomUUID().toString(),
    val tags: Set<String> = emptySet(),
    val status: ProductStatus = ProductStatus.DRAFT,
    val visibility: ProductVisibility = ProductVisibility.PRIVATE,
    val metadata: ProductMetadata,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class ProductPricing(
    val basePrice: Long,
    val salePrice: Long? = null,
    val costPrice: Long? = null,
    val currency: String = "USD",
    val taxClass: String? = null,
    val taxIncluded: Boolean = false,
    val priceValidFrom: LocalDateTime? = null,
    val priceValidTo: LocalDateTime? = null,
    val bulkPricing: List<BulkPricing> = emptyList(),
    val dynamicPricing: DynamicPricing? = null
)

data class BulkPricing(
    val minQuantity: Int,
    val maxQuantity: Int? = null,
    val price: Long,
    val discountPercentage: Long? = null
)

data class DynamicPricing(
    val enabled: Boolean = false, val parameters: Map<String, Any> = emptyMap()
)

data class ProductInventory(
    val trackInventory: Boolean = true,
    val stockQuantity: Int = 0,
    val reservedQuantity: Int = 0,
    val availableQuantity: Int = stockQuantity - reservedQuantity,
    val lowStockThreshold: Int = 10,
    val outOfStockThreshold: Int = 0,
    val backorderAllowed: Boolean = false,
    val preorderAllowed: Boolean = false,
    val stockStatus: StockStatus = StockStatus.IN_STOCK,
    val warehouseLocations: List<WarehouseStock> = emptyList(),
    val lastStockUpdate: LocalDateTime = LocalDateTime.now()
)

data class WarehouseStock(
    val warehouseId: String = UUID.randomUUID().toString(),
    val warehouseName: String,
    val quantity: Int,
    val reservedQuantity: Int = 0,
    val location: String? = null
)

data class ProductSpecifications(
    val weight: Weight? = null,
    val dimensions: Dimensions? = null,
    val color: String? = null,
    val material: String? = null,
    val customAttributes: Map<String, Any> = emptyMap(),
    val technicalSpecs: Map<String, String> = emptyMap(),
    val certifications: List<Certification> = emptyList(),
    val compatibleWith: List<String> = emptyList()
)

data class Weight(
    val value: Long, val unit: WeightUnit = WeightUnit.KG
)

data class Dimensions(
    val length: Long, val width: Long, val height: Long, val unit: DimensionUnit = DimensionUnit.CM
)

data class Certification(
    val name: String,
    val issuedBy: String,
    val certificateNumber: String? = null,
    val validFrom: LocalDateTime? = null,
    val validTo: LocalDateTime? = null
)

data class ProductMedia(
    val images: List<ProductImage> = emptyList(),
    val videos: List<ProductVideo> = emptyList(),
    val documents: List<ProductPage> = emptyList(),
    val primaryImageId: String? = null
)

data class ProductImage(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val alt: String? = null,
    val title: String? = null,
    val sortOrder: Int = 0,
    val type: ImageType = ImageType.PRODUCT,
    val thumbnailUrl: String? = null,
    val mediumUrl: String? = null,
    val largeUrl: String? = null
)

data class ProductVideo(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val duration: Int? = null,
    val type: VideoType = VideoType.PRODUCT_DEMO
)

data class ProductPage(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val name: String,
    val type: DocumentType,
    val size: Long? = null,
    val mimeType: String? = null
)

data class ProductSEO(
    val metaTitle: String? = null,
    val metaDescription: String? = null,
    val metaKeywords: Set<String> = emptySet(),
    val slug: String,
    val canonicalUrl: String? = null,
    val openGraphData: OpenGraphData? = null,
    val structuredData: Map<String, Any> = emptyMap()
)

data class OpenGraphData(
    val title: String, val description: String, val image: String? = null, val type: String = "product"
)

data class ProductShipping(
    val shippable: Boolean = true,
    val freeShipping: Boolean = false,
    val shippingClass: String? = null,
    val shippingRestrictions: List<String> = emptyList(),
    val handlingTime: Int = 1, // days
    val packageType: PackageType = PackageType.BOX,
    val hazardousMaterial: Boolean = false,
    val requiresSignature: Boolean = false,
    val dropShipping: DropShippingInfo? = null
)

data class DropShippingInfo(
    val enabled: Boolean = false,
    val supplierId: String? = null,
    val supplierSku: String? = null,
    val supplierPrice: Long? = null
)

data class ProductReviews(
    val reviewsEnabled: Boolean = true,
    val averageRating: Long = 0,
    val totalReviews: Int = 0,
    val ratingDistribution: Map<Int, Int> = emptyMap(),
    val lastReviewDate: LocalDateTime? = null
)

data class Brand(
    val id: String,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val website: String? = null,
    val slug: String
)

data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val slug: String,
    val level: Int = 0,
    val path: String,
    val imageUrl: String? = null
)

data class Seller(
    val id: String,
    val businessName: String,
    val displayName: String,
    val email: String,
    val phone: String? = null,
    val address: Address,
    val businessInfo: BusinessInfo,
    val sellerRating: SellerRating,
    val policies: SellerPolicies,
    val bankDetails: BankDetails? = null,
    val taxInfo: TaxInfo,
    val status: SellerStatus = SellerStatus.ACTIVE,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING
)

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val coordinates: Coordinates? = null
)

data class Coordinates(
    val latitude: Double, val longitude: Double
)

data class BusinessInfo(
    val businessType: BusinessType,
    val registrationNumber: String? = null,
    val taxId: String? = null,
    val website: String? = null,
    val description: String? = null,
    val yearEstablished: Int? = null,
    val employeeCount: Int? = null
)

data class SellerRating(
    val averageRating: Long = 0,
    val totalRatings: Int = 0,
    val ratingDistribution: Map<Int, Int> = emptyMap(),
    val badges: List<SellerBadge> = emptyList()
)

data class SellerBadge(
    val type: BadgeType, val name: String, val description: String, val earnedAt: LocalDateTime
)

data class SellerPolicies(
    val returnPolicy: String? = null,
    val shippingPolicy: String? = null,
    val privacyPolicy: String? = null,
    val termsOfService: String? = null,
    val warrantyPolicy: String? = null
)

data class BankDetails(
    val accountHolderName: String,
    val accountNumber: String,
    val routingNumber: String,
    val bankName: String,
    val accountType: AccountType = AccountType.CHECKING
)

data class TaxInfo(
    val taxId: String? = null,
    val vatNumber: String? = null,
    val taxExempt: Boolean = false,
    val taxJurisdictions: List<String> = emptyList()
)

data class ProductMetadata(
    val source: String? = null,
    val importId: String? = null,
    val externalIds: Map<String, String> = emptyMap(),
    val customFields: Map<String, Any> = emptyMap(),
    val flags: Set<ProductFlag> = emptySet(),
    val analytics: ProductAnalytics = ProductAnalytics()
)

data class ProductAnalytics(
    val views: Long = 0,
    val clicks: Long = 0,
    val conversions: Long = 0,
    val wishlistAdds: Long = 0,
    val cartAdds: Long = 0,
    val lastViewedAt: LocalDateTime? = null
)

enum class ProductStatus {
    DRAFT, ACTIVE, INACTIVE, ARCHIVED, OUT_OF_STOCK, DISCONTINUED
}

enum class ProductVisibility {
    PUBLIC, PRIVATE, HIDDEN, PASSWORD_PROTECTED
}

enum class StockStatus {
    IN_STOCK, LOW_STOCK, OUT_OF_STOCK, BACKORDER, PREORDER
}

enum class WeightUnit {
    G, KG, LB, OZ
}

enum class DimensionUnit {
    MM, CM, M, IN, FT
}

enum class ImageType {
    PRODUCT, VARIANT, GALLERY, THUMBNAIL, ZOOM
}

enum class VideoType {
    PRODUCT_DEMO, UNBOXING, REVIEW, TUTORIAL, ADVERTISEMENT
}

enum class DocumentType {
    MANUAL, WARRANTY, CERTIFICATE, SPECIFICATION, DATASHEET
}

enum class PackageType {
    BOX, ENVELOPE, TUBE, CUSTOM
}

enum class BusinessType {
    INDIVIDUAL, PARTNERSHIP, CORPORATION, LLC, NON_PROFIT
}

enum class SellerStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING_APPROVAL, BANNED
}

enum class VerificationStatus {
    PENDING, VERIFIED, REJECTED, EXPIRED
}

enum class BadgeType {
    TOP_SELLER, VERIFIED_SELLER, FAST_SHIPPER, EXCELLENT_SERVICE, NEW_SELLER
}

enum class AccountType {
    CHECKING, SAVINGS, BUSINESS
}

enum class ProductFlag {
    FEATURED, BESTSELLER, NEW_ARRIVAL, ON_SALE, LIMITED_EDITION, EXCLUSIVE;
}

enum class TaxClass(val percentage: Double) {
    EXEMPT(0.0),

    FIVE_PERCENT_GST(5.0),

    TWELVE_PERCENT_GST(12.0),

    EIGHTEEN_PERCENT_GST(18.0),

    TWENTY_EIGHT_PERCENT_GST(28.0),

    GOLD_THREE_PERCENT_GST(3.0),
    UNCATEGORIZED(0.0);
}