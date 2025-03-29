package com.ethyllium.searchservice.api

import com.ethyllium.searchservice.dto.request.ProductRequest
import com.ethyllium.searchservice.dto.response.ApiResponse
import com.ethyllium.searchservice.mapper.ProductMapper
import com.ethyllium.searchservice.ports.InsertProduct
import com.ethyllium.searchservice.search.BrandExtractor
import com.ethyllium.searchservice.search.SearchQueryBuilder
import com.ethyllium.searchservice.service.BrandService
import com.ethyllium.searchservice.service.ProductService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val productMapper: ProductMapper,
    private val brandService: BrandService,
    private val insertProduct: InsertProduct
) {

    @PostMapping
    fun add(@RequestBody productRequest: ProductRequest): ApiResponse {
        val product = productMapper.createProduct(productRequest)
        val res = productService.insert(product)
        if (res != null) {
            return ApiResponse.Success(res)
        }
        return ApiResponse.Error("Product not created")
    }

    @GetMapping
    fun search(@RequestParam query: String): ApiResponse {
        val brandList = brandService.getBrandList()
        val brand = BrandExtractor(brandList).extractBrand(query)
        return ApiResponse.success(productService.search(SearchQueryBuilder.getSearchRequest(query), brand))
    }
}