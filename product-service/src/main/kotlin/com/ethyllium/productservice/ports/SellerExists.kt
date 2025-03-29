package com.ethyllium.productservice.ports

interface SellerExists {
    fun sellerExists(sellerId: String): Boolean
}