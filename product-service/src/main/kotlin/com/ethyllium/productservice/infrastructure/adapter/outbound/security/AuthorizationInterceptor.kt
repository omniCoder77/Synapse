package com.ethyllium.productservice.infrastructure.adapter.outbound.security

import com.ethyllium.productservice.application.config.ConfigurationBasedRoleMapping
import com.ethyllium.productservice.application.config.SecurityProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthorizationInterceptor(
    private val roleMapping: ConfigurationBasedRoleMapping, private val securityProperties: SecurityProperties
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun isAuthorized(exchange: ServerWebExchange): Mono<Boolean> {
        val request = exchange.request
        val path = request.path.value()
        val method = request.method

        if (roleMapping.isPublicPath(path)) {
            logAuthorizationDecision(path, method, "ALLOWED_ANONYMOUS", null, null)
            return Mono.just(true)
        }

        return Mono.fromCallable {
            val result = roleMapping.getRequiredRoles(path, method)
            val userRole = request.headers.getFirst("X-User-Role")
            val userId = request.headers.getFirst("X-User-Id")

            if (!result.requiresAuthorization || result.allowAnonymous) {
                logAuthorizationDecision(path, method, "ALLOWED_ANONYMOUS", userId, result)
                return@fromCallable true
            }

            if (userRole.isNullOrBlank() || userId.isNullOrBlank()) {
                logger.warn("Missing auth headers: X-User-Role or X-User-Id")
                return@fromCallable false
            }

            // Attach context to request
            exchange.attributes["auth.userId"] = userId
            exchange.attributes["auth.userRole"] = userRole

            if (result.roles.isNotEmpty() && !result.roles.contains(userRole)) {
                logger.warn("Access denied - User $userId with role $userRole tried to access $path $method")
                return@fromCallable false
            }

            if (userRole == "SELLER") {
                return@fromCallable isSellerAuthorized(userId, path, request)
            }

            true
        }.onErrorResume { e ->
            logger.error("Authorization check failed: ${e.message}")
            Mono.just(false)
        }
    }

    private fun isSellerAuthorized(userId: String, path: String, request: ServerHttpRequest): Boolean {
        val sellerIdFromQuery = request.queryParams.getFirst("sellerId")
        if (sellerIdFromQuery != null && sellerIdFromQuery != userId) {
            logger.warn("Seller mismatch in query param: $userId != $sellerIdFromQuery")
            return false
        }

        val sellerIdFromPath = extractSellerIdFromPath(path)
        if (sellerIdFromPath != null && sellerIdFromPath != userId) {
            logger.warn("Seller mismatch in path: $userId != $sellerIdFromPath")
            return false
        }

        return true
    }

    private fun extractSellerIdFromPath(path: String): String? {
        val regex = Regex("/api/seller/([^/]+)")
        return regex.find(path)?.groupValues?.get(1)
    }

    private fun logAuthorizationDecision(
        path: String,
        method: HttpMethod,
        decision: String,
        userId: String?,
        roleResult: ConfigurationBasedRoleMapping.RoleResolutionResult?
    ) {
        if (!securityProperties.logAuthorizationAttempts) return

        when (decision) {
            "ALLOWED", "ALLOWED_ANONYMOUS" -> {
                logger.info(
                    "Authorization $decision - User: ${userId ?: "anonymous"}, Method: $method, Path: $path, " + "Pattern: ${roleResult?.matchedPattern ?: "none"}, Required: ${roleResult?.roles?.joinToString() ?: "none"}"
                )
            }

            "DENIED" -> {
                logger.warn(
                    "Authorization $decision - User: ${userId ?: "unknown"}, Method: $method, Path: $path"
                )
            }
        }
    }
}
