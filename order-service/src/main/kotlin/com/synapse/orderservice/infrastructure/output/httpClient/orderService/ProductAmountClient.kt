package com.synapse.orderservice.infrastructure.output.httpClient.orderService

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient("product-verification")
interface ProductAmountClient {
    @GetMapping("/{id}")
    fun calculatePrice(@PathVariable("id") id: String): Double?
}