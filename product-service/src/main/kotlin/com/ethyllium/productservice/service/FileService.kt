package com.ethyllium.productservice.service

import com.ethyllium.productservice.exception.FileStorageException
import com.ethyllium.productservice.ports.StoreFileUseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileService(
    @Value("\${file.upload-dir}") private val rootPath: String,
) : StoreFileUseCase {

    private val rootLocation = Paths.get(rootPath).toAbsolutePath().normalize()

    override fun storeFile(file: MultipartFile): String {
        try {
            val originalFileName: String = StringUtils.cleanPath(file.originalFilename!!)
            val fileExtension: String = getFileExtension(originalFileName)
            val uniqueFileName: String = UUID.randomUUID().toString() + "." + fileExtension

            val targetLocation: Path = rootLocation.resolve(uniqueFileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            return uniqueFileName
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file ${file.originalFilename}", ex)
        }
    }

    private fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf(".")
        if (dotIndex < 0) return ""
        return fileName.substring(dotIndex + 1)
    }
}