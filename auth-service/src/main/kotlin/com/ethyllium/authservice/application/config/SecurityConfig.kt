package com.ethyllium.authservice.application.config

import com.ethyllium.authservice.infrastructure.adapters.outbound.security.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    @Value("\${passwordEncoder.strength}") private val passwordEncoderStrength: Int,
) {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityWebFilterChain {
        return http.httpBasic { it.disable() }.formLogin { it.disable() }.csrf { it.disable() }.cors { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges.pathMatchers("/auth/**").permitAll()
                exchanges.anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()).build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(passwordEncoderStrength)
    }
}