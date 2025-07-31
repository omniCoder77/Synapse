package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.OutboxEventEntity
import reactor.core.publisher.Mono

interface OutboxEntityRepository {
    // Brand Events
    fun publishBrandCreated(brand: Brand): Mono<OutboxEventEntity>
    fun publishBrandUpdated(
        brandId: String,
        fileUrl: String? = null,
        description: String? = null,
        logoUrl: String? = null,
        website: String? = null,
        slug: String? = null,
        name: String? = null
    ): Mono<Void>

    fun publishBrandDeleted(brandId: String): Mono<Void>

    // Category Events
    fun publishCategoryCreated(category: Category): Mono<Void>
    fun publishCategoryUpdated(
        categoryId: String,
        name: String?,
        description: String?,
        slug: String?,
        parentId: String?,
        imageUrl: String? = null
    ): Mono<Void>

    fun publishCategoryDeleted(categoryId: String): Mono<Void>

    // Seller Events
    fun publishSellerCreated(seller: Seller): Mono<Void>
    fun publishSellerUpdated(
        sellerId: String,
        businessName: String?,
        displayName: String?,
        address: Address?,
        businessInfo: BusinessInfo?,
        sellerRating: SellerRating?,
        policies: SellerPolicies?,
        bankDetails: BankDetails?,
        taxInfo: TaxInfo?
    ): Mono<Void>

    fun publishSellerDeleted(sellerId: String): Mono<Void>

    // WarehouseStock Events
    fun publishWarehouseStockCreated(warehouseStock: WarehouseStock): Mono<Void>
    fun publishWarehouseStockUpdated(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Void>

    fun publishWarehouseStockDeleted(warehouseId: String): Mono<Void>

    // Product Events
    fun publishProductCreated(product: Product): Mono<Void>
    fun publishProductUpdated(product: Product): Mono<Void>
    fun publishProductDeleted(productId: String): Mono<Void>
}