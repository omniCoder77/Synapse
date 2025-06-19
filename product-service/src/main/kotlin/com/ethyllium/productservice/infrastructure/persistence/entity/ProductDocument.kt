package com.ethyllium.productservice.infrastructure.persistence.entity

import com.ethyllium.productservice.domain.entity.*
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.*
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "products")
@CompoundIndexes(
    CompoundIndex(name = "seller_status_idx", def = "{'sellerId': 1, 'status': 1}"),
    CompoundIndex(name = "category_brand_idx", def = "{'categoryId': 1, 'brandId': 1}"),
    CompoundIndex(name = "price_range_idx", def = "{'pricing.basePrice': 1, 'status': 1}"),
    CompoundIndex(name = "created_status_idx", def = "{'createdAt': -1, 'status': 1}")
)
data class ProductDocument(
    @Id val id: ObjectId? = null,
    @TextIndexed(weight = 3f) val name: String,
    @TextIndexed(weight = 2f) val description: String,
    @TextIndexed val shortDescription: String? = null,
    @Indexed(unique = true) val sku: String,
    @Indexed val barcode: String? = null,
    var brandName: String,
    var categoryName: String,
    var categoryPath: String,
    @Field("sellerId") @Indexed val sellerId: String,
    val pricing: ProductPricingDocument,
    var inventory: ProductInventoryDocument,
    val specifications: ProductSpecificationsDocument,
    val media: ProductMediaDocument,
    val seo: ProductSEODocument,
    val shipping: ProductShippingDocument,
    val averageRating: Long = 0,
    val totalReviews: Int = 0,
    val reviewsEnabled: Boolean = true,
    val variantCode: String,
    @Indexed val tags: Set<String> = emptySet(),
    @Indexed val productStatus: String,
    @Indexed val productVisibility: String,
    @Indexed val createdAt: Instant = Instant.now(),
    var updatedAt: Instant,
    val searchTerms: List<String> = emptyList(),
    val facets: Map<String, Any> = emptyMap()
) {
    fun toResponse() = ProductResponse(
        id = this.id!!.toString(),
        name = this.name,
        description = this.description,
        shortDescription = this.shortDescription,
        barcode = this.barcode,
        categoryName = this.categoryName,
        categoryPath = this.categoryPath,
        sellerId = sellerId,
        pricing = pricing.toResponse(),
        inventory = inventory.toResponse(),
        specifications = specifications.toResponse(),
        media = media.toResponse(),
        seo = seo.toResponse(),
        shipping = shipping.toResponse(),
        averageRating = averageRating,
        totalReviews = totalReviews,
        variantCode = variantCode,
        tags = tags,
        productStatus = productStatus,
        visibility = productVisibility,
        facets = facets,
        sku = sku,
        brandName = brandName,
    )

    fun toSummaryResponse() = ProductSummaryResponse(
        id = this.id!!.toString(),
        name = this.name,
        shortDescription = this.shortDescription,
        sku = this.sku,
        brandName = brandName,
        categoryName = categoryName,
        sellerId = sellerId,
        basePrice = pricing.basePrice,
        salePrice = pricing.salePrice,
        currency = pricing.currency,
        totalReviews = totalReviews,
        averageRating = averageRating,
        primaryImageUrl = media.primaryImageUrl,
        stockStatus = StockStatus.valueOf(inventory.stockStatus),
        status = ProductStatus.valueOf(productStatus),
        visibility = ProductVisibility.valueOf(productVisibility),
        tags = tags
    )
}

data class ProductPricingDocument(
    val basePrice: Long,
    val salePrice: Long? = null,
    val costPrice: Long? = null,
    val currency: String = "USD",
    val taxClass: String? = null,
    val taxIncluded: Boolean = false,
    val priceValidFrom: Instant? = null,
    val priceValidTo: Instant? = null,
    val bulkPricing: List<BulkPricingDocument> = emptyList(),
) {
    fun toResponse() = ProductPricingResponse(
        basePrice = basePrice,
        salePrice = salePrice,
        costPrice = costPrice,
        currency = currency,
        taxClass = taxClass,
        taxIncluded = taxIncluded,
        priceValidFrom = priceValidFrom?.toEpochMilli(),
        priceValidTo = priceValidTo?.toEpochMilli(),
        bulkPricing = bulkPricing.map { it.toResponse() },
    )
}

data class BulkPricingDocument(
    val minQuantity: Int, val maxQuantity: Int? = null, val price: Long, val discountPercentage: Long? = null
) {
    fun toResponse() = BulkPricingResponse(
        minQuantity = minQuantity,
        maxQuantity = maxQuantity,
        price = price,
        discountPercentage = discountPercentage,
    )
}

data class DynamicPricingDocument(
    val enabled: Boolean = false, val parameters: Map<String, Any> = emptyMap()
) {
    fun toResponse() = DynamicPricingResponse(
        enabled = enabled, parameters = parameters
    )
}

data class ProductInventoryDocument(
    val stockQuantity: Int,
    val reservedQuantity: Int = 0,
    val availableQuantity: Int = stockQuantity,
    val lowStockThreshold: Int = 0,
    val outOfStockThreshold: Int = 0,
    val backorderAllowed: Boolean = false,
    val preorderAllowed: Boolean = false,
    val stockStatus: String,
    val warehouseLocations: List<WarehouseStockDocument> = emptyList(),
    val lastStockUpdate: Instant
) {
    fun toResponse() = ProductInventoryResponse(
        stockQuantity = stockQuantity,
        reservedQuantity = reservedQuantity,
        availableQuantity = availableQuantity,
        lowStockThreshold = lowStockThreshold,
        outOfStockThreshold = outOfStockThreshold,
        preorderAllowed = preorderAllowed,
        warehouseLocations = warehouseLocations.map { it.toResponse() },
        backorderAllowed = backorderAllowed,
        lastStockUpdate = lastStockUpdate.toEpochMilli(),
        stockStatus = StockStatus.valueOf(stockStatus)
    )
}


data class ProductSpecificationsDocument(
    val weight: WeightDocument? = null,
    val dimensions: DimensionsDocument? = null,
    val color: String? = null,
    val material: String? = null,
    val customAttributes: Map<String, Any> = emptyMap(),
    val technicalSpecs: Map<String, String> = emptyMap(),
    val certifications: List<CertificationDocument> = emptyList(),
    val compatibleWith: List<String> = emptyList()
) {
    fun toResponse() = ProductSpecificationsResponse(
        weight = weight?.toResponse(),
        dimensions = dimensions?.toResponse(),
        color = color,
        material = material,
        customAttributes = customAttributes,
        technicalSpecs = technicalSpecs,
        certifications = certifications.map { it.toResponse() },
        compatibleWith = compatibleWith
    )
}

data class WeightDocument(
    val value: Long, val unit: String
) {
    fun toResponse() = WeightResponse(
        value = value, unit = WeightUnit.valueOf(unit)
    )
}

data class DimensionsDocument(
    val length: Long, val width: Long, val height: Long, val unit: String
) {
    fun toResponse() = DimensionsResponse(
        length = length, width = width, height = height, unit = DimensionUnit.valueOf(unit)
    )
}

data class CertificationDocument(
    val name: String,
    val issuedBy: String,
    val certificateNumber: String? = null,
    val validFrom: Instant? = null,
    val validTo: Instant? = null
) {
    fun toResponse() = CertificationResponse(
        name = name,
        issuedBy = issuedBy,
        certificateNumber = certificateNumber,
        validFrom = validFrom?.toEpochMilli(),
        validTo = validTo?.toEpochMilli()
    )
}

data class ProductMediaDocument(
    val images: List<ProductImageDocument> = emptyList(),
    val videos: List<ProductVideoDocument> = emptyList(),
    val documents: List<ProductDocumentFile> = emptyList(),
    val primaryImageUrl: String? = null
) {
    fun toResponse() = ProductMediaResponse(
        images = images.map { it.toResponse() },
        videos = videos.map { it.toResponse() },
        documents = documents.map { it.toResponse() },
        primaryImageUrl = primaryImageUrl
    )
}

data class ProductImageDocument(
    val url: String,
    val alt: String? = null,
    val title: String? = null,
    val sortOrder: Int = 0,
    val type: String,
    val thumbnailUrl: String? = null,
    val mediumUrl: String? = null,
    val largeUrl: String? = null
) {
    fun toResponse() = ProductImageResponse(
        url = url,
        alt = alt,
        title = title,
        sortOrder = sortOrder,
        type = ImageType.valueOf(type),
        thumbnailUrl = thumbnailUrl,
        mediumUrl = mediumUrl,
        largeUrl = largeUrl
    )
}

data class ProductVideoDocument(
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val duration: Int? = null,
    val type: String
) {
    fun toResponse() = ProductVideoResponse(
        url = url,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        duration = duration,
        type = VideoType.valueOf(type)
    )
}

data class ProductDocumentFile(
    val url: String, val name: String, val type: String, val size: Long? = null, val mimeType: String? = null
) {
    fun toResponse() = ProductDocumentResponse(
        url = url, name = name, type = DocumentType.valueOf(type), size = size, mimeType = mimeType
    )
}

data class ProductSEODocument(
    val metaTitle: String? = null,
    val metaDescription: String? = null,
    val metaKeywords: Set<String> = emptySet(),
    val canonicalUrl: String? = null,
    val openGraphData: OpenGraphDataDocument? = null,
    val structuredData: Map<String, Any> = emptyMap()
) {
    fun toResponse() = ProductSEOResponse(
        metaTitle = metaTitle,
        metaDescription = metaDescription,
        metaKeywords = metaKeywords,
        canonicalUrl = canonicalUrl,
        openGraphData = openGraphData?.toResponse(),
        structuredData = structuredData
    )
}

data class OpenGraphDataDocument(
    val title: String, val description: String, val image: String? = null, val type: String = "product"
) {
    fun toResponse() = OpenGraphDataResponse(
        title = title, description = description, image = image, type = type
    )
}

data class ProductShippingDocument(
    val shippable: Boolean = true,
    val freeShipping: Boolean = false,
    val shippingClass: String? = null,
    val shippingRestrictions: List<String> = emptyList(),
    val handlingTime: Int = 1,
    val packageType: String,
    val hazardousMaterial: Boolean = false,
    val requiresSignature: Boolean = false,
    val dropShipping: DropShippingInfoDocument? = null
) {
    fun toResponse() = ProductShippingResponse(
        shippable = shippable,
        handlingTime = handlingTime,
        freeShipping = freeShipping,
        shippingClass = shippingClass,
        shippingRestrictions = shippingRestrictions,
        packageType = PackageType.valueOf(packageType),
        hazardousMaterial = hazardousMaterial,
        requiresSignature = requiresSignature,
        dropShipping = dropShipping?.toResponse()
    )
}

data class DropShippingInfoDocument(
    val enabled: Boolean = false,
    val supplierId: String? = null,
    val supplierSku: String? = null,
    val supplierPrice: Long? = null
) {
    fun toResponse() = DropShippingInfoResponse(
        enabled = enabled, supplierId = supplierId, supplierSku = supplierSku, supplierPrice = supplierPrice
    )
}

data class AddressDocument(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val coordinates: CoordinatesDocument? = null
)

data class CoordinatesDocument(
    val latitude: Double, val longitude: Double
)

data class BusinessInfoDocument(
    val businessType: String,
    val registrationNumber: String? = null,
    val taxId: String? = null,
    val website: String? = null,
    val description: String? = null,
    val yearEstablished: Int? = null,
    val employeeCount: Int? = null
)

data class SellerRatingDocument(
    val averageRating: Long = 0,
    val totalRatings: Int = 0,
    val ratingDistribution: Map<Int, Int> = emptyMap(),
    val badges: List<SellerBadgeDocument> = emptyList()
)

data class SellerBadgeDocument(
    val type: String, val name: String, val description: String, val earnedAt: Instant
)

data class SellerPoliciesDocument(
    val returnPolicy: String? = null,
    val shippingPolicy: String? = null,
    val privacyPolicy: String? = null,
    val termsOfService: String? = null,
    val warrantyPolicy: String? = null
)

data class BankDetailsDocument(
    val accountHolderName: String,
    val accountNumber: String,
    val routingNumber: String,
    val bankName: String,
    val accountType: String
)

data class TaxInfoDocument(
    val taxId: String? = null,
    val vatNumber: String? = null,
    val taxExempt: Boolean = false,
    val taxJurisdictions: List<String> = emptyList()
)