package com.ethyllium.productservice.application.config

import com.ethyllium.productservice.infrastructure.security.AuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authorizationInterceptor: AuthorizationInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**")
            .excludePathPatterns(
                "/health/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**"
            )
    }
}
