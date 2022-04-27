package com.fictadvisor.pryomka.domain.models

import java.util.*

/** Metadata of a token
 * @param userId owner's id
 * @param validUntil date when token expires
 * @param type type of the token */
data class TokenMetadata(
    val userId: UserIdentifier,
    val validUntil: Date,
    val type: Type
) {
    enum class Type {
        /** Access token is used to receive access to the API. */
        Access,

        /** Refresh token is used to generate new Refresh and Access tokens without requiring user to login. */
        Refresh,
    }
}
