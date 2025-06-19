package com.ethyllium.authservice.infrastructure.security

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.web.rest.MFAController
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService, private val tokenService: TokenService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: run {
            filterChain.doFilter(request, response)
            return
        }
        val token = header.removePrefix("Bearer ")
        val username = validateUser(token)
        if (username != null) {
            val user = userDetailsService.loadUserByUsername(username) ?: run {
                filterChain.doFilter(request, response)
                return
            }
            val authToken = UsernamePasswordAuthenticationToken(
                user, null, user.authorities
            )
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
        }
        filterChain.doFilter(request, response)
    }

    private fun validateUser(token: String): String? {
        val claims = tokenService.getClaims(token) ?: return null
        val username = claims["username"] as String?
        if (username == null) return null
        if (claims[MFAController.MFA_VERIFIED] != null) return null
        return username
    }
}