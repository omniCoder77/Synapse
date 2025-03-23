package com.ethyllium.authservice.mfa

interface MfaPurposeHandler {
    fun handle(username: String, userId: String, vararg data: String): String
}