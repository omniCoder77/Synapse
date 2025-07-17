package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.model.WarehouseStock
import reactor.core.publisher.Mono

interface EventPublisher {
    // Brand Events
    fun publishBrandCreated(brand: Brand): Mono<Void>
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
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?
    ): Mono<Void>

    fun publishCategoryDeleted(categoryId: String): Mono<Void>

    // Seller Events
    fun publishSellerCreated(seller: Seller): Mono<Void>
    fun publishSellerUpdated(
        sellerId: String, businessName: String?, displayName: String?, phone: String?
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