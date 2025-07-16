package com.ethyllium.authservice.domain.model

sealed interface LoginAttempt {
    data class NewDeviceLogin(val token: String) : LoginAttempt
    data object MFALogin : LoginAttempt
    data object InvalidCredentials : LoginAttempt
    data object CredentialVerification : LoginAttempt
    data class Success(val token: String) : LoginAttempt
}