package com.ethyllium.productservice.exception

import java.io.IOException

class FileStorageException(message: String, ex: IOException) : RuntimeException(message, ex)
