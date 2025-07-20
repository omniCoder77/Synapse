import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.KafkaEventPublisherPublisher
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.util.Topics
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.OutboxEventEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.core.KafkaTemplate
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

class KafkaEventPublisherPublisherTest {

    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate
    private lateinit var kafkaEventPublisherPublisher: KafkaEventPublisherPublisher

    // Use a real ObjectMapper to ensure correct serialization during tests
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        reactiveMongoTemplate = mock()
        kafkaEventPublisherPublisher = KafkaEventPublisherPublisher(kafkaTemplate, reactiveMongoTemplate)

        // Mock the insert method to return a Mono<OutboxEventEntity>
        // We'll capture the argument to verify it later
        whenever(reactiveMongoTemplate.insert(any<OutboxEventEntity>())) doReturn Mono.just(mock())

        // Mock the KafkaTemplate send method to do nothing, as we'll verify calls
        whenever(kafkaTemplate.send(any<String>(), any<String>())) doReturn mock()
    }

    @Test
    fun `publishBrandCreated should create, store, and send BrandCreatedEvent`() {
        // Given
        val brand = Brand(
            id = "brand123",
            name = "Test Brand",
            website = "www.testbrand.com",
            description = "A test brand",
            logoUrl = "logo.png",
            slug = "test-brand"
        )
        val expectedEvent = BrandCreatedEvent(
            brand.id!!, brand.name, brand.website, brand.description, brand.logoUrl, brand.slug
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishBrandCreated(brand)

        // Then
        // Verify reactiveMongoTemplate.insert was called with the correct OutboxEventEntity
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.BRAND_CREATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }

        verify(kafkaTemplate).send(Topics.BRAND_CREATED, expectedKafkaEvent)
    }

    @Test
    fun `publishBrandUpdated should create, store, and send BrandUpdatedEvent`() {
        // Given
        val brandId = "brand123"
        val name = "Updated Brand Name"
        val website = "new.testbrand.com"
        val expectedEvent = BrandUpdatedEvent(
            brandId = brandId,
            fileUrl = null,
            description = null,
            logoUrl = null,
            website = website,
            slug = null,
            name = name
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishBrandUpdated(
            brandId = brandId,
            name = name,
            website = website,
            fileUrl = null,
            description = null,
            logoUrl = null,
            slug = null
        )

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.BRAND_UPDATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.BRAND_UPDATED, expectedKafkaEvent)
    }

    @Test
    fun `publishBrandDeleted should create, store, and send BrandDeletedEvent`() {
        // Given
        val brandId = "brand123"
        val expectedEvent = BrandDeletedEvent(brandId = brandId)
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishBrandDeleted(brandId)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.BRAND_DELETED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.BRAND_DELETED, expectedKafkaEvent)
    }

    @Test
    fun `publishCategoryCreated should create, store, and send CategoryCreatedEvent`() {
        // Given
        val category = Category(
            id = "cat123",
            name = "Electronics",
            description = "Electronics items",
            parentId = null,
            slug = "electronics",
            level = 1,
            path = "/electronics",
            imageUrl = "image.png"
        )
        val expectedEvent = CategoryCreatedEvent(
            id = category.id!!,
            name = category.name,
            description = category.description,
            parentId = category.parentId,
            slug = category.slug,
            level = category.level,
            path = category.path,
            imageUrl = category.imageUrl
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishCategoryCreated(category)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.CATEGORY_CREATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.CATEGORY_CREATED, expectedKafkaEvent)
    }

    @Test
    fun `publishCategoryUpdated should create, store, and send CategoryUpdatedEvent`() {
        // Given
        val categoryId = "cat123"
        val name = "Updated Electronics"
        val description = "Updated description"
        val expectedEvent = CategoryUpdatedEvent(
            categoryId = categoryId, name = name, description = description, slug = null, parentId = null,
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishCategoryUpdated(
            categoryId = categoryId, name = name, description = description, slug = null, parentId = null
        )

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.CATEGORY_UPDATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.CATEGORY_UPDATED, expectedKafkaEvent)
    }

    @Test
    fun `publishCategoryDeleted should create, store, and send CategoryDeletedEvent`() {
        // Given
        val categoryId = "cat123"
        val expectedEvent = CategoryDeletedEvent(categoryId)
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishCategoryDeleted(categoryId)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.CATEGORY_DELETED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.CATEGORY_DELETED, expectedKafkaEvent)
    }

    @Test
    fun `publishSellerCreated should create, store, and send SellerCreatedEvent`() {
        // Given
        val seller = Seller(
            id = "seller123",
            businessName = "Test Business",
            displayName = "Test Seller",
            email = "seller@test.com",
            phone = "1234567890",
            address = Address(
                street = "123 Main St", city = "Anytown", state = "Anystate", postalCode = "12345", country = "USA"
            ),
            businessInfo = BusinessInfo(
                registrationNumber = "REG123", taxId = "TAX123", businessType = BusinessType.INDIVIDUAL,
            ),
            sellerRating = SellerRating(),
            policies = SellerPolicies(
                shippingPolicy = "Ships in 2 days", returnPolicy = "30-day returns"
            ),
            bankDetails = BankDetails(
                bankName = "Bank of America",
                accountNumber = "123456789",
                routingNumber = "987654321",
                accountType = AccountType.BUSINESS,
                accountHolderName = "New Account Holder"
            ),
            taxInfo = TaxInfo(
                taxId = "GST123"
            )
        )
        val expectedEvent = SellerCreatedEvent(
            id = seller.id,
            businessName = seller.businessName,
            displayName = seller.displayName,
            email = seller.email,
            phone = seller.phone,
            address = seller.address,
            businessInfo = seller.businessInfo,
            sellerRating = seller.sellerRating,
            policies = seller.policies,
            bankDetails = seller.bankDetails,
            taxInfo = seller.taxInfo
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishSellerCreated(seller)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.SELLER_CREATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.SELLER_CREATED, expectedKafkaEvent)
    }

    @Test
    fun `publishSellerUpdated should create, store, and send SellerUpdatedEvent`() {
        // Given
        val sellerId = "seller123"
        val displayName = "New Display Name"
        val expectedEvent = SellerUpdatedEvent(
            sellerId = sellerId, businessName = null, displayName = displayName, phone = null
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishSellerUpdated(
            sellerId = sellerId, businessName = null, displayName = displayName, phone = null
        )

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.SELLER_UPDATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.SELLER_UPDATED, expectedKafkaEvent)
    }

    @Test
    fun `publishSellerDeleted should create, store, and send SellerDeletedEvent`() {
        // Given
        val sellerId = "seller123"
        val expectedEvent = SellerDeletedEvent(sellerId)
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishSellerDeleted(sellerId)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.SELLER_DELETED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.SELLER_DELETED, expectedKafkaEvent)
    }

    @Test
    fun `publishWarehouseStockCreated should create, store, and send WarehouseStockCreatedEvent`() {
        // Given
        val warehouseStock = WarehouseStock(
            warehouseId = "warehouse123",
            warehouseName = "Main Warehouse",
            quantity = 100,
            reservedQuantity = 10,
            location = "Aisle 1, Rack 2"
        )
        val expectedEvent = WarehouseStockCreatedEvent(
            warehouseId = warehouseStock.warehouseId,
            warehouseName = warehouseStock.warehouseName,
            quantity = warehouseStock.quantity,
            reservedQuantity = warehouseStock.reservedQuantity,
            location = warehouseStock.location
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishWarehouseStockCreated(warehouseStock)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.WAREHOUSE_CREATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.WAREHOUSE_CREATED, expectedKafkaEvent)
    }

    @Test
    fun `publishWarehouseStockUpdated should create, store, and send WarehouseStockUpdatedEvent`() {
        // Given
        val warehouseId = "warehouse123"
        val quantity = 120
        val reservedQuantity = 15
        val expectedEvent = WarehouseStockUpdatedEvent(
            warehouseId = warehouseId, quantity = quantity, reservedQuantity = reservedQuantity, location = null
        )
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishWarehouseStockUpdated(
            warehouseId = warehouseId, quantity = quantity, reservedQuantity = reservedQuantity, location = null
        )

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.WAREHOUSE_UPDATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.WAREHOUSE_UPDATED, expectedKafkaEvent)
    }

    @Test
    fun `publishWarehouseStockDeleted should create, store, and send WarehouseStockDeletedEvent`() {
        // Given
        val warehouseId = "warehouse123"
        val expectedEvent = WarehouseStockDeletedEvent(warehouseId)
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishWarehouseStockDeleted(warehouseId)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.WAREHOUSE_DELETED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.WAREHOUSE_DELETED, expectedKafkaEvent)
    }

    @Test
    fun `publishProductCreated should create, store, and send ProductCreatedEvent`() {
        // Given
        val expectedEvent = createMockProduct()
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent.toKafkaEvent())

        // When
        kafkaEventPublisherPublisher.publishProductCreated(expectedEvent)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.PRODUCT_CREATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.PRODUCT_CREATED, expectedKafkaEvent)
    }

    @Test
    fun `publishProductUpdated should create, store, and send ProductUpdatedEvent`() {
        // Given
        val expectedEvent = createMockProduct()
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent.toKafkaEvent())

        // When
        kafkaEventPublisherPublisher.publishProductUpdated(expectedEvent)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.PRODUCT_UPDATED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.PRODUCT_UPDATED, expectedKafkaEvent)
    }

    fun createMockProduct(): Product {
        val brand = Brand(
            id = UUID.randomUUID().toString(),
            name = "Ethyllium Electronics",
            description = "Leading manufacturer of high-quality electronics.",
            logoUrl = "https://example.com/ethyllium-logo.png",
            website = "https://www.ethyllium.com",
            slug = "ethyllium-electronics"
        )

        val category = Category(
            id = UUID.randomUUID().toString(),
            name = "Smartphones",
            description = "Latest smartphones and mobile devices.",
            parentId = null,
            slug = "smartphones",
            level = 1,
            path = "electronics/smartphones",
            imageUrl = "https://example.com/smartphone-category.png"
        )

        val seller = Seller(
            id = UUID.randomUUID().toString(),
            businessName = "Global Tech Solutions Inc.",
            displayName = "GlobalTech Store",
            email = "seller@globaltech.com",
            phone = "+1-555-123-4567",
            address = Address(
                street = "123 Tech Lane",
                city = "Techville",
                state = "CA",
                postalCode = "90210",
                country = "USA",
                coordinates = Coordinates(latitude = 34.0522, longitude = -118.2437)
            ),
            businessInfo = BusinessInfo(
                businessType = BusinessType.CORPORATION,
                registrationNumber = "GTS-987654321",
                taxId = "TAX-ID-12345",
                website = "https://www.globaltechstore.com",
                description = "Your one-stop shop for all things tech.",
                yearEstablished = 2010,
                employeeCount = 150
            ),
            sellerRating = SellerRating(
                averageRating = 4,
                totalRatings = 1500,
                ratingDistribution = mapOf(5 to 1000, 4 to 300, 3 to 150, 2 to 30, 1 to 20),
                badges = listOf(
                    SellerBadge(
                        BadgeType.TOP_SELLER,
                        "Top Seller",
                        "Achieved top sales performance.",
                        LocalDateTime.now().minusMonths(6)
                    ), SellerBadge(
                        BadgeType.FAST_SHIPPER,
                        "Fast Shipper",
                        "Consistently ships orders quickly.",
                        LocalDateTime.now().minusMonths(3)
                    )
                )
            ),
            policies = SellerPolicies(
                returnPolicy = "30-day no-hassle returns.",
                shippingPolicy = "Ships within 1-2 business days.",
                privacyPolicy = "Your privacy is important to us.",
                termsOfService = "Standard terms and conditions apply.",
                warrantyPolicy = "1-year manufacturer warranty."
            ),
            bankDetails = BankDetails(
                accountHolderName = "Global Tech Solutions Inc.",
                accountNumber = "1234567890",
                routingNumber = "098765432",
                bankName = "First National Bank",
                accountType = AccountType.BUSINESS
            ),
            taxInfo = TaxInfo(
                taxId = "US123456789", vatNumber = null, taxExempt = false, taxJurisdictions = listOf("US-CA")
            ),
            status = SellerStatus.ACTIVE,
            verificationStatus = VerificationStatus.VERIFIED
        )

        val pricing = ProductPricing(
            basePrice = 99999, // $999.99
            salePrice = 89999, // $899.99
            costPrice = 70000, // $700.00
            currency = "USD",
            taxClass = TaxClass.EIGHTEEN_PERCENT_GST.name, // Using name as per your enum string type
            taxIncluded = false,
            priceValidFrom = LocalDateTime.now().minusDays(7),
            priceValidTo = LocalDateTime.now().plusMonths(1),
            bulkPricing = listOf(
                BulkPricing(minQuantity = 5, maxQuantity = 9, price = 85000, discountPercentage = 15),
                BulkPricing(minQuantity = 10, price = 80000, discountPercentage = 20)
            ),
            dynamicPricing = DynamicPricing(
                enabled = true, parameters = mapOf("algorithm" to "demand_based", "factor" to 0.95)
            )
        )

        val inventory = ProductInventory(
            trackInventory = true,
            stockQuantity = 50,
            reservedQuantity = 5,
            lowStockThreshold = 15,
            outOfStockThreshold = 0,
            backorderAllowed = false,
            preorderAllowed = false,
            stockStatus = StockStatus.IN_STOCK,
            warehouseLocations = listOf(
                WarehouseStock(
                    warehouseName = "Main Warehouse A",
                    quantity = 30,
                    reservedQuantity = 3,
                    location = "Aisle 5, Shelf 10"
                ), WarehouseStock(
                    warehouseName = "Distribution Center B",
                    quantity = 20,
                    reservedQuantity = 2,
                    location = "Zone C, Rack 7"
                )
            ),
            lastStockUpdate = LocalDateTime.now()
        )

        val specifications = ProductSpecifications(
            weight = Weight(value = 200, unit = WeightUnit.G),
            dimensions = Dimensions(length = 150, width = 75, height = 8, unit = DimensionUnit.MM),
            color = "Midnight Black",
            material = "Aluminum and Glass",
            customAttributes = mapOf("Processor" to "Octa-core", "RAM" to "8GB", "Storage" to "128GB"),
            technicalSpecs = mapOf("Display" to "6.1-inch OLED", "Camera" to "48MP Dual", "Battery" to "4000mAh"),
            certifications = listOf(
                Certification(
                    name = "CE Certified",
                    issuedBy = "European Conformity",
                    certificateNumber = "CE-12345",
                    validFrom = LocalDateTime.now().minusYears(1),
                    validTo = LocalDateTime.now().plusYears(4)
                )
            ),
            compatibleWith = listOf("Android 15", "5G Networks")
        )

        val media = ProductMedia(
            images = listOf(
                ProductImage(
                    url = "https://example.com/product-image-1.jpg",
                    alt = "Front view of smartphone",
                    type = ImageType.PRODUCT,
                    sortOrder = 1
                ), ProductImage(
                    url = "https://example.com/product-image-2.jpg",
                    alt = "Back view of smartphone",
                    type = ImageType.GALLERY,
                    sortOrder = 2,
                    thumbnailUrl = "https://example.com/product-image-2-thumb.jpg"
                ), ProductImage(
                    url = "https://example.com/product-image-3.jpg",
                    alt = "Smartphone packaging",
                    type = ImageType.GALLERY,
                    sortOrder = 3
                )
            ), videos = listOf(
                ProductVideo(
                    url = "https://example.com/product-video-1.mp4",
                    title = "Smartphone Demo",
                    type = VideoType.PRODUCT_DEMO,
                    duration = 120,
                    thumbnailUrl = "https://example.com/product-video-1-thumb.jpg"
                )
            ), documents = listOf(
                ProductPage(
                    url = "https://example.com/user-manual.pdf",
                    name = "User Manual",
                    type = DocumentType.MANUAL,
                    size = 2048000,
                    mimeType = "application/pdf"
                )
            ), primaryImageId = null // Will be assigned based on the first image's ID if needed
        )

        // Set primaryImageId after image creation
        val updatedMedia = media.copy(primaryImageId = media.images.firstOrNull()?.id)

        val seo = ProductSEO(
            metaTitle = "Ethyllium X1 - Premium Android Smartphone",
            metaDescription = "Buy the new Ethyllium X1 with 48MP camera, 8GB RAM, and long-lasting battery. Shop now!",
            metaKeywords = setOf("smartphone", "android phone", "ethyllium x1", "mobile", "electronics"),
            slug = "ethyllium-x1-premium-smartphone",
            canonicalUrl = "https://www.ethyllium.com/products/ethyllium-x1",
            openGraphData = OpenGraphData(
                title = "Ethyllium X1 - Premium Android Smartphone",
                description = "Buy the new Ethyllium X1 with 48MP camera, 8GB RAM, and long-lasting battery. Shop now!",
                image = "https://example.com/product-image-1.jpg",
                type = "product"
            ),
            structuredData = mapOf(
                "@context" to "https://schema.org",
                "@type" to "Product",
                "name" to "Ethyllium X1",
                "description" to "Premium Android Smartphone",
                "sku" to "ET-X1-BL-128",
                "brand" to mapOf("@type" to "Brand", "name" to brand.name),
                "image" to listOf("https://example.com/product-image-1.jpg"),
                "offers" to mapOf(
                    "@type" to "Offer",
                    "priceCurrency" to pricing.currency,
                    "price" to pricing.salePrice?.toDouble()?.div(100), // Convert cents to dollars
                    "itemCondition" to "https://schema.org/NewCondition",
                    "availability" to "https://schema.org/InStock"
                )
            )
        )

        val shipping = ProductShipping(
            shippable = true,
            freeShipping = false,
            shippingClass = "STANDARD_ELECTRONICS",
            shippingRestrictions = listOf("No international shipping for batteries"),
            handlingTime = 2,
            packageType = PackageType.BOX,
            hazardousMaterial = true,
            requiresSignature = true,
            dropShipping = DropShippingInfo(
                enabled = false
            )
        )

        val reviews = ProductReviews(
            reviewsEnabled = true,
            averageRating = 4,
            totalReviews = 100,
            ratingDistribution = mapOf(5 to 70, 4 to 20, 3 to 7, 2 to 2, 1 to 1),
            lastReviewDate = LocalDateTime.now().minusDays(5)
        )

        val metadata = ProductMetadata(
            source = "Internal ERP",
            importId = "ERP-PROD-001",
            externalIds = mapOf("amazon" to "B0XXXXXX", "ebay" to "ITEMYYYYYY"),
            customFields = mapOf("warranty_period_months" to 12, "color_family" to "black"),
            flags = setOf(ProductFlag.FEATURED, ProductFlag.NEW_ARRIVAL),
            analytics = ProductAnalytics(
                views = 15000,
                clicks = 5000,
                conversions = 200,
                wishlistAdds = 500,
                cartAdds = 300,
                lastViewedAt = LocalDateTime.now().minusHours(1)
            )
        )

        return Product(
            id = UUID.randomUUID().toString(),
            name = "Ethyllium X1 - Premium Android Smartphone",
            description = "Experience the future of mobile technology with the Ethyllium X1. Boasting a stunning OLED display, powerful processor, and an advanced 48MP dual-camera system, it's designed for seamless performance and breathtaking photography. Stay connected with 5G capabilities and enjoy all-day battery life. Available in Midnight Black.",
            shortDescription = "A powerful and elegant Android smartphone with a stunning display and advanced camera.",
            sku = "ET-X1-BL-128",
            barcode = "1234567890123",
            brand = brand,
            category = category,
            seller = seller,
            pricing = pricing,
            inventory = inventory,
            specifications = specifications,
            media = updatedMedia,
            seo = seo,
            shipping = shipping,
            reviews = reviews,
            variantCode = UUID.randomUUID().toString(),
            tags = setOf("smartphone", "android", "5g", "mobile", "premium"),
            status = ProductStatus.ACTIVE,
            visibility = ProductVisibility.PUBLIC,
            metadata = metadata,
            createdAt = LocalDateTime.now().minusMonths(3),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `publishProductDeleted should create, store, and send ProductDeletedEvent`() {
        // Given
        val productId = "prod123"
        val expectedEvent = ProductDeletedEvent(productId)
        val expectedKafkaEvent = objectMapper.writeValueAsString(expectedEvent)

        // When
        kafkaEventPublisherPublisher.publishProductDeleted(productId)

        // Then
        argumentCaptor<OutboxEventEntity>().apply {
            verify(reactiveMongoTemplate).insert(capture())
            val capturedOutboxEvent = firstValue
            assert(capturedOutboxEvent.eventTopic == Topics.PRODUCT_DELETED)
            assert(capturedOutboxEvent.payload == expectedKafkaEvent)
        }
        verify(kafkaTemplate).send(Topics.PRODUCT_DELETED, expectedKafkaEvent)
    }
}
