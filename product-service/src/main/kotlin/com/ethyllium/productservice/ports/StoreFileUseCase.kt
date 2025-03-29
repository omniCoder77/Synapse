package com.ethyllium.productservice.ports

import org.springframework.web.multipart.MultipartFile

interface StoreFileUseCase {
    fun storeFile(file: MultipartFile): String
}