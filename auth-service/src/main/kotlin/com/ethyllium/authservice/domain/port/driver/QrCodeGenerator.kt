package com.ethyllium.authservice.domain.port.driver

interface QrCodeGenerator {
    fun generateQrCode(uri: String, width: Int = 200, height: Int = 200): ByteArray
}