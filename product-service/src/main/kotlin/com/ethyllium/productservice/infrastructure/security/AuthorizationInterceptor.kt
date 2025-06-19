package com.ethyllium.productservice.infrastructure.security

import com.ethyllium.productservice.domain.annotations.RequireRoles
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthorizationInterceptor : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val requireRoles = handler.getMethodAnnotation(RequireRoles::class.java)
            if (requireRoles != null) {
                return checkAuthorization(request, requireRoles.roles)
            }
        }
        return true
    }

    private fun checkAuthorization(request: HttpServletRequest, requiredRoles: Array<String>): Boolean {
        try {
            val userRole = request.getHeader("X-User-Role") ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Missing user role"
            )

            val userId = request.getHeader("X-User-Id") ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Missing user ID"
            )

            logger.debug("Authorization check - User: $userId, Role: $userRole, Required roles: ${requiredRoles.joinToString()}")

            if (!requiredRoles.contains(userRole)) {
                logger.warn("Access denied - User $userId with role $userRole tried to access endpoint requiring ${requiredRoles.joinToString()}")
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions")
            }
            if (userRole == "SELLER") {
                return checkSellerAuthorization(request, userId)
            }

            return true

        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Authorization check failed", ex)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authorization check failed")
        }
    }

    private fun checkSellerAuthorization(request: HttpServletRequest, userId: String): Boolean {
        val sellerId = request.getParameter("sellerId")
        if (sellerId != null && sellerId != userId) {
            logger.warn("Seller authorization failed - User $userId tried to access seller $sellerId")
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "Sellers can only perform operations for themselves"
            )
        }
        return true
    }
}