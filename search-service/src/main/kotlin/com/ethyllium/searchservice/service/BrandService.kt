package com.ethyllium.searchservice.service

import com.ethyllium.searchservice.client.ProductClient
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class BrandService(private val productClient: ProductClient) {

    @Cacheable(value = ["brandList"], key = "'brandList'")
    fun getBrandList(): List<String> {
        val brandsString = productClient.getBrandList()
        return brandsString.split(",")
    }
}
