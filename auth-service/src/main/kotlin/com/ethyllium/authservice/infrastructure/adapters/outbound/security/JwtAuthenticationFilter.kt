package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller.MFAController
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: AuthUserDetailService, private val tokenService: TokenService
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        val token = authHeader.removePrefix("Bearer ")

        // The reactive way to process a token and set security context
        return Mono.justOrEmpty(tokenService.getSubject(token)).flatMap { username ->
                userDetailsService.findByUsername(username).flatMap { userDetails ->
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        // This is the correct way to populate the security context in a reactive chain
                        chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                    }
            }
            // If any step above is empty (e.g., token invalid or user not found), continue the chain without authentication
            .switchIfEmpty(chain.filter(exchange))
    }

    private fun validateUser(token: String): String? {
        val claims = tokenService.getClaims(token) ?: return null
        val username = claims["username"] as String?
        if (username == null) return null
        if (claims[MFAController.MFA_VERIFIED] != null) return null
        return username
    }
}