package com.ethyllium.productservice.model

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.math.BigDecimal
import java.util.*

@Entity
@Table(
    name = "products",
    indexes = [Index(name = "idx_product_name", columnList = "name"), Index(
        name = "idx_product_seller",
        columnList = "sellerId"
    ), Index(name = "idx_product_price", columnList = "price")]
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val productId: String = UUID.randomUUID().toString(),

    @field:NotBlank @field:Size(min = 2, max = 100) val name: String,

    @field:NotBlank @field:Size(max = 1000) val description: String,

    @field:Min(0) val price: Double = 0.0,

    val discount: BigDecimal? = null,

    @field:NotBlank val sellerId: String,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) @JoinColumn(name = "product_id") @Cache(usage = CacheConcurrencyStrategy.READ_WRITE) val reviews: List<Review> = listOf(),

    @ElementCollection @CollectionTable(
        name = "product_images",
        joinColumns = [JoinColumn(name = "product_id")]
    ) @Column(name = "image_url") val images: List<String> = listOf()
)