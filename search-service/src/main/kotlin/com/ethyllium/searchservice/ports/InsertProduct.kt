package com.ethyllium.searchservice.ports

import com.ethyllium.searchservice.model.Product

interface InsertProduct {
    fun insert(product: Product): Product?
}