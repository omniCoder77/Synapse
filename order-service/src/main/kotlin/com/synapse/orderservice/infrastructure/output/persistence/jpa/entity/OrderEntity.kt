package com.synapse.orderservice.infrastructure.output.persistence.jpa.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.synapse.orderservice.domain.model.*
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id val orderId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    @Enumerated(EnumType.STRING) val status: OrderStatus = OrderStatus.PENDING,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true) val items: MutableList<OrderItemEntity> = mutableListOf(),
    @Embedded val pricing: OrderPricingEmbeddable = OrderPricingEmbeddable(0.0),

    @Embedded @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "shipping_street")),
        AttributeOverride(name = "city", column = Column(name = "shipping_city")),
        AttributeOverride(name = "state", column = Column(name = "shipping_state")),
        AttributeOverride(name = "postalCode", column = Column(name = "shipping_postal_code")),
        AttributeOverride(name = "country", column = Column(name = "shipping_country"))
    ) val shippingAddress: AddressEmbeddable = AddressEmbeddable(),

    @Embedded @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "billing_street")),
        AttributeOverride(name = "city", column = Column(name = "billing_city")),
        AttributeOverride(name = "state", column = Column(name = "billing_state")),
        AttributeOverride(name = "postalCode", column = Column(name = "billing_postal_code")),
        AttributeOverride(name = "country", column = Column(name = "billing_country"))
    ) val billingAddress: AddressEmbeddable = AddressEmbeddable(),

    @Enumerated(EnumType.STRING) val paymentMethod: PaymentMethod = PaymentMethod.ONLINE,
    @Enumerated(EnumType.STRING) val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val trackingId: String = UUID.randomUUID().toString(),
    @CreationTimestamp val createdAt: Instant = Instant.now(),
    @UpdateTimestamp val updatedAt: Instant = createdAt,
    @Version val version: Int = 0,
    @Convert(converter = OrderMetadataConverter::class) val metadata: OrderMetadata = OrderMetadata()
)

@Converter
class OrderMetadataConverter : AttributeConverter<OrderMetadata, String> {
    private val objectMapper = ObjectMapper().registerModule(kotlinModule())

    override fun convertToDatabaseColumn(attribute: OrderMetadata?): String {
        return objectMapper.writeValueAsString(attribute ?: OrderMetadata())
    }

    override fun convertToEntityAttribute(dbData: String?): OrderMetadata {
        return dbData?.let { objectMapper.readValue(it, OrderMetadata::class.java) } ?: OrderMetadata()
    }
}

@Entity
data class OrderItemEntity(
    @Id val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val currency: String = "USD",
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val imageUrl: String? = null
)

@Embeddable
data class OrderPricingEmbeddable(
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shippingCost: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = subtotal + tax + shippingCost - discount,
    val currency: String = "USD"
) {
    fun toDomain(): OrderPricing = OrderPricing(
        subtotal = Money(subtotal, currency),
        tax = Money(tax, currency),
        shippingCost = Money(shippingCost, currency),
        discount = Money(discount, currency),
        total = Money(total, currency)
    )
}

@Embeddable
data class AddressEmbeddable(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = ""
) {
    fun toDomain(): Address = Address(street, city, state, postalCode, country)
}