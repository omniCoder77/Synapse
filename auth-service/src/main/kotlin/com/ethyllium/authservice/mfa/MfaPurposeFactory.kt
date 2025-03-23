package com.ethyllium.authservice.mfa

import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.stereotype.Component

@Component
class MfaPurposeFactory(
    private val loginHandler: LoginHandler,
    private val resetPasswordPurposeHandler: ResetPasswordPurposeHandler,
    private val newDeviceLoginPurposeHandler: NewDeviceLoginPurposeHandler,
    private val unblockPurposeHandler: UnblockPurposeHandler
) {
    fun getMfaPurposeHandler(mfaPurpose: MfaPurpose): MfaPurposeHandler {
        return when (mfaPurpose) {
            MfaPurpose.LOGIN -> loginHandler
            MfaPurpose.RESET_PASSWORD -> resetPasswordPurposeHandler
            MfaPurpose.NEW_DEVICE_LOGIN -> newDeviceLoginPurposeHandler
            MfaPurpose.UNBLOCK_USER -> unblockPurposeHandler
        }
    }
}