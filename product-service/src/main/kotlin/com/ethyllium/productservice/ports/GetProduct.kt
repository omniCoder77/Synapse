package com.ethyllium.productservice.ports

import com.ethyllium.productservice.model.Product

interface GetProduct {
    fun getProductById(id: String): Product?
}