package com.ethyllium.productservice.exception

class SellerNotFoundException(sellerId: String) : RuntimeException("Seller with $sellerId not found")