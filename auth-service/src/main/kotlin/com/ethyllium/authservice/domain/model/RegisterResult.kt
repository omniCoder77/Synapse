package com.ethyllium.authservice.domain.model

sealed class RegisterResult {
    data class Token(val accessToken: String, val refreshToken: String) : RegisterResult()
    data class MfaImage(val mfaQrCode: ByteArray) : RegisterResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MfaImage

            return mfaQrCode.contentEquals(other.mfaQrCode)
        }

        override fun hashCode(): Int {
            return mfaQrCode.contentHashCode()
        }
    }

    data class Failure(val error: String) : RegisterResult()
}