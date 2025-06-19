package com.ethyllium.productservice.domain.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireRoles(val roles: Array<String>)