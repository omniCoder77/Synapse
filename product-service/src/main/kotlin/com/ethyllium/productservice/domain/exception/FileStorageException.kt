package com.ethyllium.productservice.domain.exception

import java.io.IOException

class FileStorageException(message: String, ex: IOException) : RuntimeException(message, ex)
