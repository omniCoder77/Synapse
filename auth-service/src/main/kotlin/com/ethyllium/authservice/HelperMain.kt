package com.ethyllium.authservice

//import java.io.File
//import java.security.KeyStore
//import java.util.*
//import javax.crypto.SecretKey
//
//fun main() {
//    val keyStoreLocation = "/home/rishabh/keystore.jks"
//    val keyStorePassword = "asfj3489urwejjfe4r"
//    val keyAlias = "jwtKey"
//    val keyPassword = "328942urijdfkj()87"
//
//    try {
//        // Load keystore
//        val keyStore = KeyStore.getInstance("JCEKS")
//        File(keyStoreLocation).inputStream().use { fis ->
//            keyStore.load(fis, keyStorePassword.toCharArray())
//        }
//
//        // Extract the secret key
//        val keyProtection = KeyStore.PasswordProtection(keyPassword.toCharArray())
//        val keyEntry = keyStore.getEntry(keyAlias, keyProtection) as KeyStore.SecretKeyEntry
//        val secretKey: SecretKey = keyEntry.secretKey
//
//        // Get key material
//        val keyBytes = secretKey.encoded
//        val keyBase64 = Base64.getEncoder().encodeToString(keyBytes)
//        val algorithm = secretKey.algorithm
//
//        println("Algorithm: $algorithm")
//        println("Key Material (Base64): $keyBase64")
//        println("Key Format: ${secretKey.format}")
//
//        // Store in Vault
//        println("\nVault command:")
//        println("vault kv put secret/auth-service/jwt \\")
//        println("  key-material=\"$keyBase64\" \\")
//        println("  algorithm=\"$algorithm\"")
//
//    } catch (e: Exception) {
//        println("Error extracting key: ${e.message}")
//        e.printStackTrace()
//    }
//}