package com.fictadvisor.pryomka.domain.models

import java.util.*

data class TokenMetadata(
    val userId: UserIdentifier,
    val validUntil: Date,
    val type: Type
) {
    enum class Type {
        Access,
        Refresh,
    }
}
