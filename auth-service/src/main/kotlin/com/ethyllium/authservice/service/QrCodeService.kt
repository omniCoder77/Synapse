package com.ethyllium.authservice.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class QrCodeService {

    fun generateQrCode(uri: String, width: Int = 200, height: Int = 200): ByteArray {
        val matrix = MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, width, height)
        val outputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream)
        return outputStream.toByteArray()
    }
}