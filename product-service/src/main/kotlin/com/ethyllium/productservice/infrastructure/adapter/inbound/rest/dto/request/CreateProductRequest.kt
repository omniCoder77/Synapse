package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.*
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.ZoneOffset

data class CreateProductRequest(
    @field:NotBlank(message = "Product name is required") @field:Size(
        min = 2, max = 255, message = "Product name must be between 2 and 255 characters"
    ) val name: String,
    @field:NotBlank(message = "Product description is required") @field:Size(
        min = 10, max = 5000, message = "Product description must be between 10 and 5000 characters"
    ) val description: String,
    @field:Size(
        max = 500, message = "Short description must not exceed 500 characters"
    ) val shortDescription: String? = null,
    @field:NotBlank(message = "SKU is required") @field:Pattern(
        regexp = "^[A-Z0-9][A-Z0-9-_]{2,19}$",
        message = "SKU must contain only uppercase letters, numbers, hyphens, and underscores"
    ) val sku: String,
    @field:Pattern(regexp = "^[0-9]{8,15}$", message = "Barcode must be 8-15 digits") val barcode: String? = null,
    @field:NotBlank(message = "Brand ID is required") val brandId: String,
    val categoryId: String,
    val categoryPath: String,
    @field:Valid @field:NotNull(message = "Pricing information is required") val pricing: ProductPricingRequest,
    @field:Valid val inventory: ProductInventoryRequest = ProductInventoryRequest(),
    @field:Valid val specifications: ProductSpecificationsRequest = ProductSpecificationsRequest(),
    @field:Valid val media: ProductMediaRequest = ProductMediaRequest(),
    @field:Valid @field:NotNull(message = "SEO information is required") val seo: ProductSEORequest,
    @field:Valid val shipping: ProductShippingRequest = ProductShippingRequest(),
    val tags: Set<String> = emptySet(),
    val status: ProductStatus = ProductStatus.DRAFT,
    val visibility: ProductVisibility = ProductVisibility.PRIVATE,
    val reviewsEnabled: Boolean = true,
    val variantOfId: String = "",
    val searchTerms: List<String> = emptyList(),
    val facets: Map<String, Any> = emptyMap(),
    val variantCode: String
) {
    fun toDomain(sellerId: String): Product {
        return Product(
            name = this.name,
            description = this.description,
            shortDescription = this.shortDescription,
            sku = this.sku,
            barcode = this.barcode,
            brand = Brand(id = this.brandId, name = "", slug = "", ownerId = ""),
            category = Category(id = this.categoryId, name = "", slug = "", path = this.categoryPath),
            seller = Seller(
                id = sellerId,
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
            seo = this.seo.toDomain(),
            shipping = this.shipping.toDomain(),
            tags = this.tags,
            status = this.status,
            visibility = this.visibility,
            variantCode = this.variantCode,
            reviews = ProductReviews(reviewsEnabled = this.reviewsEnabled),
            metadata = ProductMetadata()
        )
    }
}

fun ProductPricingRequest.toDomain(): ProductPricing = ProductPricing(
    basePrice = (this.basePrice * 100).toLong(),
    salePrice = (this.salePrice * 100).toLong(),
    costPrice = this.costPrice?.let { (it * 100).toLong() },
    currency = this.currency,
    taxClass = this.taxClass.name,
    taxIncluded = this.taxIncluded,
    priceValidFrom = this.priceValidFrom?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime() },
    priceValidTo = this.priceValidTo?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime() },
    bulkPricing = this.bulkPricing.map { it.toDomain() })

fun BulkPricingRequest.toDomain(): BulkPricing = BulkPricing(minQuantity, maxQuantity, price, discountPercentage)
fun ProductInventoryRequest.toDomain(): ProductInventory = ProductInventory(
    trackInventory,
    stockQuantity,
    reservedQuantity,
    stockQuantity - reservedQuantity,
    lowStockThreshold,
    outOfStockThreshold,
    backorderAllowed,
    preorderAllowed,
    StockStatus.valueOf(stockStatus.ifBlank { "IN_STOCK" }),
    warehouseLocations.map { it.toDomain() })

fun WarehouseStockRequest.toDomain(): WarehouseStock = WarehouseStock(
    warehouseName = warehouseName, quantity = quantity, reservedQuantity = reservedQuantity, location = location
)

fun ProductSpecificationsRequest.toDomain(): ProductSpecifications = ProductSpecifications(
    weight?.toDomain(),
    dimensions?.toDomain(),
    color,
    material,
    customAttributes,
    technicalSpecs,
    certifications.map { it.toDomain() },
    compatibleWith
)

fun WeightRequest.toDomain(): Weight = Weight(value, unit)
fun DimensionsRequest.toDomain(): Dimensions = Dimensions(length, width, height, unit)
fun CertificationRequest.toDomain(): Certification = Certification(
    name,
    issuedBy,
    certificateNumber,
    Instant.ofEpochMilli(validFrom).atZone(ZoneOffset.UTC).toLocalDateTime(),
    Instant.ofEpochMilli(validTo).atZone(ZoneOffset.UTC).toLocalDateTime()
)

fun ProductMediaRequest.toDomain(): ProductMedia =
    ProductMedia(images.map { it.toDomain() }, videos.map { it.toDomain() }, documents.map { it.toDomain() })

fun ProductImageRequest.toDomain(): ProductImage =
    ProductImage(url = url, alt = alt, title = title, sortOrder = sortOrder, type = type)

fun ProductVideoRequest.toDomain(): ProductVideo = ProductVideo(
    url = url, title = title, description = description, thumbnailUrl = thumbnailUrl, duration = duration, type = type
)

fun ProductDocumentRequest.toDomain(): ProductPage =
    ProductPage(url = url, name = name, type = type, size = size, mimeType = mimeType)

fun ProductSEORequest.toDomain(): ProductSEO =
    ProductSEO(metaTitle, metaDescription, metaKeywords, "", canonicalUrl, openGraphData?.toDomain(), structuredData)

fun OpenGraphDataRequest.toDomain(): OpenGraphData = OpenGraphData(title, description, image, type)
fun ProductShippingRequest.toDomain(): ProductShipping = ProductShipping(
    shippable,
    freeShipping,
    shippingClass,
    shippingRestrictions,
    handlingTime,
    packageType,
    hazardousMaterial,
    requiresSignature,
    dropShipping?.toDomain()
)

fun DropShippingInfoRequest.toDomain(): DropShippingInfo =
    DropShippingInfo(enabled, supplierId, supplierSku, supplierPrice)