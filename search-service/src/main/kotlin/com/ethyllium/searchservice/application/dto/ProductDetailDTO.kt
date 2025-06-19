package com.ethyllium.searchservice.application.dto

import com.ethyllium.searchservice.domain.model.Product

data class ProductDetailDTO(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String?,
    val sku: String,
    val brand: BrandDTO,
    val category: CategoryDTO,
    val pricing: ProductPriceDTO,
    val inventory: ProductInventoryDTO,
    val specifications: ProductSpecsDTO,
    val media: ProductMediaDTO,
    val seo: ProductSeoDTO,
    val tags: Set<String>,
    val status: String,
    val visibility: String,
) {
    companion object {
        fun fromDomain(product: Product): ProductDetailDTO {
            return ProductDetailDTO(
                id = product.id,
                name = product.name,
                description = product.description,
                shortDescription = product.shortDescription,
                sku = product.sku,
                brand = BrandDTO.fromDomain(product.brand),
                category = CategoryDTO.fromDomain(product.category),
                pricing = ProductPriceDTO.Companion.fromDomain(product.pricing),
                inventory = ProductInventoryDTO.fromDomain(product.inventory),
                specifications = ProductSpecsDTO.fromDomain(product.specifications),
                media = ProductMediaDTO.fromDomain(product.media),
                seo = ProductSeoDTO.fromDomain(product.seo),
                tags = product.tags,
                status = product.status.toString(),
                visibility = product.visibility.toString(),
            )
        }
    }
}

data class ProductInventoryDTO(
    val stockQuantity: Int,
    val availableQuantity: Int,
    val stockStatus: String,
    val lowStockThreshold: Int
) {
    companion object {
        fun fromDomain(inventory: Product.SearchInventory): ProductInventoryDTO {
            return ProductInventoryDTO(
                stockQuantity = inventory.stockQuantity,
                availableQuantity = inventory.availableQuantity,
                stockStatus = inventory.stockStatus.toString(),
                lowStockThreshold = inventory.lowStockThreshold
            )
        }
    }
}

data class ProductSpecsDTO(
    val weight: ProductWeightDTO?,
    val dimensions: ProductDimensionsDTO?,
    val color: String?,
    val material: String?,
    val technicalSpecs: Map<String, String>
) {
    companion object {
        fun fromDomain(specs: Product.SearchSpecifications): ProductSpecsDTO {
            return ProductSpecsDTO(
                weight = specs.weight?.let { ProductWeightDTO.fromDomain(it) },
                dimensions = specs.dimensions?.let { ProductDimensionsDTO.fromDomain(it) },
                color = specs.color,
                material = specs.material,
                technicalSpecs = specs.technicalSpecs
            )
        }
    }
}

data class ProductWeightDTO(
    val value: Long,
    val unit: String
) {
    companion object {
        fun fromDomain(weight: Product.SearchSpecifications.SearchWeight): ProductWeightDTO {
            return ProductWeightDTO(
                value = weight.value,
                unit = weight.unit.toString()
            )
        }
    }
}

data class ProductDimensionsDTO(
    val length: Long,
    val width: Long,
    val height: Long,
    val unit: String
) {
    companion object {
        fun fromDomain(dimensions: Product.SearchSpecifications.SearchDimensions): ProductDimensionsDTO {
            return ProductDimensionsDTO(
                length = dimensions.length,
                width = dimensions.width,
                height = dimensions.height,
                unit = dimensions.unit.toString()
            )
        }
    }
}

data class ProductMediaDTO(
    val images: List<ProductImageDTO>,
    val primaryImageUrl: String?
) {
    companion object {
        fun fromDomain(media: Product.SearchMedia): ProductMediaDTO {
            return ProductMediaDTO(
                images = media.images.map { ProductImageDTO.fromDomain(it) },
                primaryImageUrl = media.primaryImageUrl
            )
        }
    }
}

data class ProductImageDTO(
    val url: String,
    val alt: String?
) {
    companion object {
        fun fromDomain(image: Product.SearchMedia.SearchImage): ProductImageDTO {
            return ProductImageDTO(
                url = image.url,
                alt = image.alt
            )
        }
    }
}

data class ProductSeoDTO(
    val metaTitle: String?,
    val metaDescription: String?,
    val slug: String
) {
    companion object {
        fun fromDomain(seo: Product.SearchSEO): ProductSeoDTO {
            return ProductSeoDTO(
                metaTitle = seo.metaTitle,
                metaDescription = seo.metaDescription,
                slug = seo.slug
            )
        }
    }
}