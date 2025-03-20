package com.ethyllium.searchservice.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "product-service")
interface ProductClient {
    @Cacheable(value = ["brandList"], key = "'brandList'")
    @GetMapping
    fun getBrandList(): String
}