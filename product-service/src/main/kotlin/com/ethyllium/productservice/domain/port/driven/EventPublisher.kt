package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.*

interface EventPublisher {
    // Brand Events
    fun publishBrandCreated(brand: Brand)
    fun publishBrandUpdated(
        brandId: String,
        fileUrl: String? = null,
        description: String? = null,
        logoUrl: String? = null,
        website: String? = null,
        slug: String? = null,
        name: String? = null
    )

    fun publishBrandDeleted(brandId: String)

    // Category Events
    fun publishCategoryCreated(category: Category)
    fun publishCategoryUpdated(
        categoryId: String,
        name: String?,
        description: String?,
        slug: String?,
        parentId: String?,
        imageUrl: String? = null
    )

    fun publishCategoryDeleted(categoryId: String)

    // Seller Events
    fun publishSellerCreated(seller: Seller)
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
    )

    fun publishSellerDeleted(sellerId: String)

    // WarehouseStock Events
    fun publishWarehouseStockCreated(warehouseStock: WarehouseStock)
    fun publishWarehouseStockUpdated(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    )

    fun publishWarehouseStockDeleted(warehouseId: String)

    // Product Events
    fun publishProductCreated(product: Product)
    fun publishProductUpdated(product: Product)
    fun publishProductDeleted(productId: String)
}