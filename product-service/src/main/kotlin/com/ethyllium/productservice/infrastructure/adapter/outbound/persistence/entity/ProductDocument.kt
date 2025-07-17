package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event.ProductCreated
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Document(collection = "products")
data class ProductDocument(
    @Id val id: ObjectId? = null,
    val name: String,
    val description: String,
    val shortDescription: String? = null,
    @Indexed(unique = true, background = true) val sku: String,
    val barcode: String? = null,
    var categoryPath: String,
    val sellerId: String,
    val pricing: ProductPricingDocument,
    var inventory: ProductInventoryDocument,
    val specifications: ProductSpecificationsDocument,
    val media: ProductMediaDocument,
    val seo: ProductSEODocument,
    val shipping: ProductShippingDocument,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val reviewsEnabled: Boolean = true,
    val variantCode: String? = null,
    val tags: Set<String> = emptySet(),
    val productStatus: String,
    val productVisibility: String,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val searchTerms: List<String> = emptyList(),
    val facets: Map<String, Any> = emptyMap(),
    val isActive: Boolean = true,
    val isInStock: Boolean = true,
    val saleEndDate: Instant? = null,
    val lastModifiedBy: String? = null,
    val categoryId: String?,
    val brandId: String
) {
    fun toResponse() = ProductResponse(
        id = this.id!!.toString(),
        name = this.name,
        description = this.description,
        shortDescription = this.shortDescription,
        barcode = this.barcode,
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
        categoryId = categoryId
    )
}

data class ProductPricingDocument(
    val basePrice: Double,
    val salePrice: Double = basePrice,
    val costPrice: Double? = null,
    val currency: String = "USD",
    val taxClass: TaxClass = TaxClass.EXEMPT,
    val taxIncluded: Boolean = false,
    val priceValidFrom: Instant? = null,
    val priceValidTo: Instant? = null,
    val bulkPricing: List<BulkPricingDocument> = emptyList(),
) {
    val discountPercentage: Double
        get() {
            if (salePrice < basePrice) {
                return ((basePrice - salePrice) / basePrice) * 100
            }
            return 0.0
        }

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
) {
    val primaryImageUrl: String

    init {
        if (images.isEmpty()) {
            throw IllegalArgumentException("At least one image is required for ProductMediaDocument.")
        }
        primaryImageUrl = images[0].url
    }

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

fun ProductDocument.toDomain(): Product = Product(
    id = this.id!!.toHexString(),
    name = this.name,
    description = this.description,
    shortDescription = this.shortDescription,
    sku = this.sku,
    barcode = this.barcode,
    brand = Brand(id = this.brandId, name = "", slug = ""),
    category = Category(id = this.categoryId, name = "", slug = "", path = this.categoryPath),
    seller = Seller(
        id = this.sellerId,
        businessName = "",
        displayName = "",
        email = "",
        address = Address("", "", "", "", ""),
        businessInfo = BusinessInfo(BusinessType.INDIVIDUAL),
        policies = SellerPolicies(),
        taxInfo = TaxInfo(),
        sellerRating = SellerRating()
    ),
    pricing = this.pricing.toDomain(),
    inventory = this.inventory.toDomain(),
    specifications = this.specifications.toDomain(),
    media = this.media.toDomain(),
    seo = this.seo.toDomain(this.sku),
    shipping = this.shipping.toDomain(),
    reviews = ProductReviews(
        reviewsEnabled = this.reviewsEnabled,
        averageRating = this.averageRating.toLong(),
        totalReviews = this.totalReviews
    ),
    variantCode = this.variantCode ?: UUID.randomUUID().toString(),
    tags = this.tags,
    status = ProductStatus.valueOf(this.productStatus),
    visibility = ProductVisibility.valueOf(this.productVisibility),
    metadata = ProductMetadata(), // Assuming default metadata
    createdAt = LocalDateTime.ofInstant(this.createdAt, ZoneOffset.UTC),
    updatedAt = LocalDateTime.ofInstant(this.updatedAt, ZoneOffset.UTC)
)

fun Product.toDocument(): ProductDocument = ProductDocument(
    id = if (this.id!!.isNotEmpty() && ObjectId.isValid(this.id)) ObjectId(this.id) else null,
    name = this.name,
    description = this.description,
    shortDescription = this.shortDescription,
    sku = this.sku,
    barcode = this.barcode,
    categoryPath = this.category.path,
    sellerId = this.seller.id,
    pricing = this.pricing.toDocument(),
    inventory = this.inventory.toDocument(),
    specifications = this.specifications.toDocument(),
    media = this.media.toDocument(),
    seo = this.seo.toDocument(),
    shipping = this.shipping.toDocument(),
    averageRating = this.reviews.averageRating.toDouble(),
    totalReviews = this.reviews.totalReviews,
    reviewsEnabled = this.reviews.reviewsEnabled,
    variantCode = this.variantCode,
    tags = this.tags,
    productStatus = this.status.name,
    productVisibility = this.visibility.name,
    createdAt = this.createdAt.toInstant(ZoneOffset.UTC),
    updatedAt = this.updatedAt.toInstant(ZoneOffset.UTC),
    categoryId = this.category.id,
    brandId = this.brand.id ?: ""
)


fun ProductPricingDocument.toDomain(): ProductPricing = ProductPricing(
    basePrice = (this.basePrice * 100).toLong(), // Convert to cents/long
    salePrice = (this.salePrice * 100).toLong(),
    costPrice = this.costPrice?.let { (it * 100).toLong() },
    currency = this.currency,
    taxClass = this.taxClass.name,
    taxIncluded = this.taxIncluded,
    priceValidFrom = this.priceValidFrom?.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) },
    priceValidTo = this.priceValidTo?.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) },
    bulkPricing = this.bulkPricing.map { it.toDomain() })

fun ProductPricing.toDocument(): ProductPricingDocument =
    ProductPricingDocument(
        basePrice = this.basePrice / 100.0, // Convert from cents/long
        salePrice = this.salePrice?.let { it / 100.0 } ?: (this.basePrice / 100.0),
        costPrice = this.costPrice?.let { it / 100.0 },
        currency = this.currency,
        taxClass = TaxClass.valueOf(this.taxClass ?: "UNCATEGORIZED"),
        taxIncluded = this.taxIncluded,
        priceValidFrom = this.priceValidFrom?.toInstant(ZoneOffset.UTC),
        priceValidTo = this.priceValidTo?.toInstant(ZoneOffset.UTC),
        bulkPricing = this.bulkPricing.map { it.toDocument() })

fun BulkPricingDocument.toDomain(): BulkPricing = BulkPricing(
    minQuantity = this.minQuantity,
    maxQuantity = this.maxQuantity,
    price = this.price,
    discountPercentage = this.discountPercentage
)

fun BulkPricing.toDocument(): BulkPricingDocument = BulkPricingDocument(
    minQuantity = this.minQuantity,
    maxQuantity = this.maxQuantity,
    price = this.price,
    discountPercentage = this.discountPercentage
)

fun ProductInventoryDocument.toDomain(): ProductInventory = ProductInventory(
    stockQuantity = this.stockQuantity,
    reservedQuantity = this.reservedQuantity,
    availableQuantity = this.availableQuantity,
    lowStockThreshold = this.lowStockThreshold,
    outOfStockThreshold = this.outOfStockThreshold,
    backorderAllowed = this.backorderAllowed,
    preorderAllowed = this.preorderAllowed,
    stockStatus = StockStatus.valueOf(this.stockStatus),
    warehouseLocations = this.warehouseLocations.map { it.toDomain() },
    lastStockUpdate = LocalDateTime.ofInstant(this.lastStockUpdate, ZoneOffset.UTC)
)

fun ProductInventory.toDocument(): ProductInventoryDocument = ProductInventoryDocument(
    stockQuantity = this.stockQuantity,
    reservedQuantity = this.reservedQuantity,
    availableQuantity = this.availableQuantity,
    lowStockThreshold = this.lowStockThreshold,
    outOfStockThreshold = this.outOfStockThreshold,
    backorderAllowed = this.backorderAllowed,
    preorderAllowed = this.preorderAllowed,
    stockStatus = this.stockStatus.name,
    warehouseLocations = this.warehouseLocations.map { it.toDocument() },
    lastStockUpdate = this.lastStockUpdate.toInstant(ZoneOffset.UTC)
)

fun ProductSpecificationsDocument.toDomain(): ProductSpecifications = ProductSpecifications(
    weight = this.weight?.toDomain(),
    dimensions = this.dimensions?.toDomain(),
    color = this.color,
    material = this.material,
    customAttributes = this.customAttributes,
    technicalSpecs = this.technicalSpecs,
    certifications = this.certifications.map { it.toDomain() },
    compatibleWith = this.compatibleWith
)

fun ProductSpecifications.toDocument(): ProductSpecificationsDocument = ProductSpecificationsDocument(
    weight = this.weight?.toDocument(),
    dimensions = this.dimensions?.toDocument(),
    color = this.color,
    material = this.material,
    customAttributes = this.customAttributes,
    technicalSpecs = this.technicalSpecs,
    certifications = this.certifications.map { it.toDocument() },
    compatibleWith = this.compatibleWith
)

fun WeightDocument.toDomain(): Weight = Weight(value = this.value, unit = WeightUnit.valueOf(this.unit))
fun Weight.toDocument(): WeightDocument = WeightDocument(value = this.value, unit = this.unit.name)

fun DimensionsDocument.toDomain(): Dimensions =
    Dimensions(length = this.length, width = this.width, height = this.height, unit = DimensionUnit.valueOf(this.unit))

fun Dimensions.toDocument(): DimensionsDocument =
    DimensionsDocument(length = this.length, width = this.width, height = this.height, unit = this.unit.name)

fun CertificationDocument.toDomain(): Certification = Certification(
    name = this.name,
    issuedBy = this.issuedBy,
    certificateNumber = this.certificateNumber,
    validFrom = this.validFrom?.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) },
    validTo = this.validTo?.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) })

fun Certification.toDocument(): CertificationDocument = CertificationDocument(
    name = this.name,
    issuedBy = this.issuedBy,
    certificateNumber = this.certificateNumber,
    validFrom = this.validFrom?.toInstant(ZoneOffset.UTC),
    validTo = this.validTo?.toInstant(ZoneOffset.UTC)
)

fun ProductMediaDocument.toDomain(): ProductMedia = ProductMedia(
    images = this.images.map { it.toDomain() },
    videos = this.videos.map { it.toDomain() },
    documents = this.documents.map { it.toDomain() },
    primaryImageId = this.images.firstOrNull()?.url
)

fun ProductMedia.toDocument(): ProductMediaDocument = ProductMediaDocument(
    images = this.images.map { it.toDocument() },
    videos = this.videos.map { it.toDocument() },
    documents = this.documents.map { it.toDocument() })

fun ProductImageDocument.toDomain(): ProductImage = ProductImage(
    url = this.url, alt = this.alt, title = this.title, sortOrder = this.sortOrder, type = ImageType.valueOf(this.type)
)

fun ProductImage.toDocument(): ProductImageDocument = ProductImageDocument(
    url = this.url,
    alt = this.alt,
    title = this.title,
    sortOrder = this.sortOrder,
    type = this.type.name,
    thumbnailUrl = this.thumbnailUrl,
    mediumUrl = this.mediumUrl,
    largeUrl = this.largeUrl
)

fun ProductImage.toKafkaProductImage() = ProductCreated.ProductImage(
    url = this.url,
    alt = this.alt,
)

fun ProductVideoDocument.toDomain(): ProductVideo = ProductVideo(
    url = this.url,
    title = this.title,
    description = this.description,
    thumbnailUrl = this.thumbnailUrl,
    duration = this.duration,
    type = VideoType.valueOf(this.type)
)

fun ProductVideo.toDocument(): ProductVideoDocument = ProductVideoDocument(
    url = this.url,
    title = this.title,
    description = this.description,
    thumbnailUrl = this.thumbnailUrl,
    duration = this.duration,
    type = this.type.name
)

fun ProductDocumentFile.toDomain(): ProductPage = ProductPage(
    url = this.url, name = this.name, type = DocumentType.valueOf(this.type), size = this.size, mimeType = this.mimeType
)

fun ProductPage.toDocument(): ProductDocumentFile = ProductDocumentFile(
    url = this.url, name = this.name, type = this.type.name, size = this.size, mimeType = this.mimeType
)

fun ProductSEODocument.toDomain(slug: String): ProductSEO = ProductSEO(
    metaTitle = this.metaTitle,
    metaDescription = this.metaDescription,
    metaKeywords = this.metaKeywords,
    slug = slug,
    canonicalUrl = this.canonicalUrl,
    openGraphData = this.openGraphData?.toDomain(),
    structuredData = this.structuredData
)

fun ProductSEO.toDocument(): ProductSEODocument = ProductSEODocument(
    metaTitle = this.metaTitle,
    metaDescription = this.metaDescription,
    metaKeywords = this.metaKeywords,
    canonicalUrl = this.canonicalUrl,
    openGraphData = this.openGraphData?.toDocument(),
    structuredData = this.structuredData
)

fun OpenGraphDataDocument.toDomain(): OpenGraphData = OpenGraphData(
    title = this.title, description = this.description, image = this.image, type = this.type
)

fun OpenGraphData.toDocument(): OpenGraphDataDocument = OpenGraphDataDocument(
    title = this.title, description = this.description, image = this.image, type = this.type
)

fun ProductShippingDocument.toDomain(): ProductShipping = ProductShipping(
    shippable = this.shippable,
    freeShipping = this.freeShipping,
    shippingClass = this.shippingClass,
    shippingRestrictions = this.shippingRestrictions,
    handlingTime = this.handlingTime,
    packageType = PackageType.valueOf(this.packageType),
    hazardousMaterial = this.hazardousMaterial,
    requiresSignature = this.requiresSignature,
    dropShipping = this.dropShipping?.toDomain()
)

fun ProductShipping.toDocument(): ProductShippingDocument = ProductShippingDocument(
    shippable = this.shippable,
    freeShipping = this.freeShipping,
    shippingClass = this.shippingClass,
    shippingRestrictions = this.shippingRestrictions,
    handlingTime = this.handlingTime,
    packageType = this.packageType.name,
    hazardousMaterial = this.hazardousMaterial,
    requiresSignature = this.requiresSignature,
    dropShipping = this.dropShipping?.toDocument()
)

fun DropShippingInfoDocument.toDomain(): DropShippingInfo = DropShippingInfo(
    enabled = this.enabled,
    supplierId = this.supplierId,
    supplierSku = this.supplierSku,
    supplierPrice = this.supplierPrice
)

fun DropShippingInfo.toDocument(): DropShippingInfoDocument = DropShippingInfoDocument(
    enabled = this.enabled,
    supplierId = this.supplierId,
    supplierSku = this.supplierSku,
    supplierPrice = this.supplierPrice
)