package com.ethyllium.productservice.domain.exception

class SellerNotFoundException(sellerId: String) : RuntimeException("Seller with $sellerId not found")