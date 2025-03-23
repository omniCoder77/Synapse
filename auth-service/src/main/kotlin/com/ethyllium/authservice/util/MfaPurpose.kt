package com.ethyllium.authservice.util

enum class MfaPurpose {
    LOGIN,
    RESET_PASSWORD,
    NEW_DEVICE_LOGIN,
    UNBLOCK_USER;

    companion object {
        fun get(purpose: String): MfaPurpose {
            return when (purpose) {
                LOGIN.name -> LOGIN
                RESET_PASSWORD.name -> RESET_PASSWORD
                NEW_DEVICE_LOGIN.name -> NEW_DEVICE_LOGIN
                UNBLOCK_USER.name -> UNBLOCK_USER
                else -> throw IllegalArgumentException("MFA Purpose $purpose is not valid")
            }
        }
    }
}