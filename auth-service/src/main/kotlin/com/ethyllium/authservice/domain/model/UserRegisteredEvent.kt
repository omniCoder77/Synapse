package com.ethyllium.authservice.domain.model

import java.util.UUID

data class UserRegisteredEvent(val userId: UUID, val email: String, val deviceFingerprint: String, val password: String)