package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.mapper

import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductMetadata
import com.ethyllium.productservice.domain.model.ProductReviews
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.toDomain
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.BrandDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.CategoryDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.SellerDocument
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ProductMapper(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    fun toDomain(createProductRequest: CreateProductRequest, sellerId: String): Product {
        val brandMono = reactiveMongoTemplate.findOne(
            Query.query(Criteria.where("_id").`is`(createProductRequest.brandId)), BrandDocument::class.java
        )
        val sellerMono = reactiveMongoTemplate.findOne(
            Query.query(Criteria.where("_id").`is`(sellerId)), SellerDocument::class.java
        )
        val catMono = reactiveMongoTemplate.findOne(
            Query.query(Criteria.where("_id").`is`(createProductRequest.brandId)), CategoryDocument::class.java
        )
        return Mono.zip(sellerMono, catMono, brandMono).map {
            Product(
                name = createProductRequest.name,
                description = createProductRequest.description,
                shortDescription = createProductRequest.shortDescription,
                sku = createProductRequest.sku,
                barcode = createProductRequest.barcode,
                brand = it.t3.toDomain(),
                category = it.t2.toDomain(),
                pricing = createProductRequest.pricing.toDomain(),
                inventory = createProductRequest.inventory.toDomain(),
                specifications = createProductRequest.specifications.toDomain(),
                media = createProductRequest.media.toDomain(),
                seo = createProductRequest.seo.toDomain(),
                shipping = createProductRequest.shipping.toDomain(),
                tags = createProductRequest.tags,
                status = createProductRequest.status,
                visibility = createProductRequest.visibility,
                variantCode = createProductRequest.variantOfId,
                seller = it.t1.toDomain(),
                reviews = ProductReviews(createProductRequest.reviewsEnabled),
                metadata = ProductMetadata()
            )
        }.block()!!
    }
}