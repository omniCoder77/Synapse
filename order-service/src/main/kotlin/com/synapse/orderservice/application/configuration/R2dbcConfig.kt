package com.synapse.orderservice.application.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.synapse.orderservice.domain.model.Address
import io.r2dbc.postgresql.codec.Json
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager

@Configuration
class R2dbcConfig {

    @Bean
    fun r2dbcCustomConversions(objectMapper: ObjectMapper): R2dbcCustomConversions {
        val converters = listOf(
            AddressToJsonConverter(objectMapper),
            JsonToAddressConverter(objectMapper)
        )
        return R2dbcCustomConversions.of(org.springframework.data.r2dbc.dialect.PostgresDialect.INSTANCE, converters)
    }

    @WritingConverter
    class AddressToJsonConverter(private val objectMapper: ObjectMapper) : Converter<Address, Json> {
        override fun convert(source: Address): Json {
            return Json.of(objectMapper.writeValueAsString(source))
        }
    }

    @ReadingConverter
    class JsonToAddressConverter(private val objectMapper: ObjectMapper) : Converter<Json, Address> {
        override fun convert(source: Json): Address {
            return objectMapper.readValue(source.asString(), Address::class.java)
        }
    }

        @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}