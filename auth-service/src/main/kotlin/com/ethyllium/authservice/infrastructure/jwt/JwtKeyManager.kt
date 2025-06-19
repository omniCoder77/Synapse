package com.ethyllium.authservice.infrastructure.jwt

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Component
class JwtKeyManager(
    @Value("\${jwt.keystore.location:jwtKeystore.jceks}") private var keyStoreFilePath: String,
    @Value("\${jwt.keystore.password}") private var keyStorePasswordStr: String,
    @Value("\${jwt.key.alias:jwtKey}") private var keyAlias: String,
    @Value("\${jwt.key.password}") private var keyPasswordStr: String
) {

    private var keyStore: KeyStore = KeyStore.getInstance("JCEKS")
    private var cachedKey: SecretKey? = null

    @PostConstruct
    fun init() {
        val keyStorePassword = keyStorePasswordStr.toCharArray()

        val keyStoreFile = File(keyStoreFilePath)
        if (keyStoreFile.exists()) {
            FileInputStream(keyStoreFile).use { fis ->
                keyStore.load(fis, keyStorePassword)
            }
        } else {
            keyStore.load(null, keyStorePassword)
            val secretKey = generateSecretKey()
            keyStore.setEntry(
                keyAlias, KeyStore.SecretKeyEntry(secretKey), KeyStore.PasswordProtection(keyPasswordStr.toCharArray())
            )
            FileOutputStream(keyStoreFile).use { fos ->
                keyStore.store(fos, keyStorePassword)
            }
        }
    }

    private fun generateSecretKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("HmacSHA256")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    fun getKey(): SecretKey {
        if (cachedKey != null) return cachedKey!!
        else {
            val keyEntry = keyStore.getEntry(keyAlias, KeyStore.PasswordProtection(keyPasswordStr.toCharArray()))
            if (keyEntry is KeyStore.SecretKeyEntry) {
                cachedKey = keyEntry.secretKey
                return cachedKey!!
            }
        }
        throw IllegalStateException("Key not found or wrong type for alias: $keyAlias")
    }
}
