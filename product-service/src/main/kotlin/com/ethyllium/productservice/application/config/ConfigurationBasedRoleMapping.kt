package com.ethyllium.productservice.application.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import javax.annotation.PostConstruct

@Component
class ConfigurationBasedRoleMapping(
    private val securityProperties: SecurityProperties
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pathMatcher = AntPathMatcher()

    // Cache for performance - pattern to role config mapping
    private val roleConfigCache = mutableMapOf<String, RoleConfig>()

    data class RoleConfig(
        val methods: Set<HttpMethod>,
        val roles: Array<String>,
        val allowAnonymous: Boolean = false
    )

    @PostConstruct
    fun initializeRoleMapping() {
        logger.info("Initializing role mappings from configuration...")

        securityProperties.endpoints.forEach { (pattern, config) ->
            val methods = config.methods.mapNotNull { methodName ->
                try {
                    HttpMethod.valueOf(methodName.uppercase())
                } catch (e: IllegalArgumentException) {
                    logger.warn("Invalid HTTP method '{}' for pattern '{}'", methodName, pattern)
                    null
                }
            }.toSet()

            if (methods.isEmpty()) {
                logger.warn("No valid HTTP methods found for pattern '{}', defaulting to all methods", pattern)
            }

            val roleConfig = RoleConfig(
                methods = methods.ifEmpty { HttpMethod.values().toSet() },
                roles = config.roles.toTypedArray(),
                allowAnonymous = config.allowAnonymous
            )

            roleConfigCache[pattern] = roleConfig
            logger.debug("Mapped pattern '{}' to roles: {} for methods: {}",
                pattern, config.roles, methods.map { it.name() })
        }

        logger.info("Initialized {} endpoint security configurations", roleConfigCache.size)
    }

    fun getRequiredRoles(path: String, method: HttpMethod): RoleResolutionResult {
        // Find matching patterns with priority (more specific patterns first)
        val matches = roleConfigCache.entries
            .filter { (pattern, config) ->
                pathMatcher.match(pattern, path) &&
                (config.methods.isEmpty() || config.methods.contains(method))
            }
            .sortedBy { (pattern, _) ->
                // More specific patterns (fewer wildcards) get higher priority
                getPatternSpecificity(pattern)
            }

        val bestMatch = matches.firstOrNull()

        return if (bestMatch != null) {
            val (pattern, config) = bestMatch
            logger.debug("Pattern '{}' matched path '{}' with method '{}'", pattern, path, method)

            RoleResolutionResult(
                roles = config.roles,
                allowAnonymous = config.allowAnonymous,
                matchedPattern = pattern,
                requiresAuthorization = config.roles.isNotEmpty() && !config.allowAnonymous
            )
        } else {
            // Default: require authentication but no specific roles
            RoleResolutionResult(
                roles = emptyArray(),
                allowAnonymous = false,
                matchedPattern = null,
                requiresAuthorization = true
            )
        }
    }

    private fun getPatternSpecificity(pattern: String): Int {
        // Lower score means higher specificity
        return pattern.count { it == '*' } * 10 +
               pattern.count { it == '?' } * 5 +
               if (pattern.endsWith("**")) 20 else 0
    }

    fun isPublicPath(path: String): Boolean {
        return securityProperties.publicPaths.any { pattern ->
            pathMatcher.match(pattern, path)
        }
    }

    data class RoleResolutionResult(
        val roles: Array<String>,
        val allowAnonymous: Boolean,
        val matchedPattern: String?,
        val requiresAuthorization: Boolean
    )
}