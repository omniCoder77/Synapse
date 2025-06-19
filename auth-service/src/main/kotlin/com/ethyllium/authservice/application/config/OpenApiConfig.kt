package com.ethyllium.authservice.application.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@OpenAPIDefinition
@Configuration
class OpenApiConfig {
    @Bean
    fun baseOpenAPI(): OpenAPI {
        val badRequest = ApiResponse().content(
            Content().addMediaType(
                "application/json",
                MediaType().addExamples(
                    "default",
                    Example().value("{\"code\" : 400, \"status\" : \"Bad Request\", \"Message\" : \"Bad Request\"}")
                )
            )
        )
        val internalServerError = ApiResponse().content(
            Content().addMediaType(
                "application/json",
                MediaType().addExamples(
                    "default",
                    Example().value("{\"code\" : 500, \"status\" : \"internalServerError\", \"Message\" : \"internalServerError\"}")
                )
            )
        )
        val successfulResponse = ApiResponse().content(
            Content().addMediaType(
                "application/json",
                MediaType().addExamples(
                    "default",
                    Example().value("{\"name\":\"string\",\"surname\":\"string\",\"age\":0}")
                )
            )
        )
        val components = Components()
        components.addResponses("badRequest", badRequest)
        components.addResponses("internalServerError", internalServerError)
        components.addResponses("successfulResponse", successfulResponse)
        return OpenAPI().components(components).info(
            Info().title("Springboot_Swagger Project OpenAPI Docs").version("1.0.0").description("Doc Description")
        )
    }
}