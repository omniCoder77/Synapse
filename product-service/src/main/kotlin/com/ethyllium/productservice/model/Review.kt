package com.ethyllium.productservice.model

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.*

@Entity
@Table(name = "reviews")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Review(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val reviewId: UUID = UUID.randomUUID(),

    @field:NotBlank val review: String = "",

    val description: String = "",

    @field:Min(1) @field:Max(5) val rating: Int = 0,

    @field:NotBlank val userName: String = "",

    @ElementCollection @CollectionTable(name = "review_images", joinColumns = [JoinColumn(name = "review_id")]) @Column(
        name = "image_url"
    ) val images: List<String> = listOf()
)