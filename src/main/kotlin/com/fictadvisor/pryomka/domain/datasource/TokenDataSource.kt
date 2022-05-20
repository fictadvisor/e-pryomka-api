package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.TokenMetadata

/** Provides methods for work with security tokens in application. */
interface TokenDataSource {
    /** Saves given token with given metadata in the database. */
    suspend fun saveToken(token: String, metadata: TokenMetadata): Int

    /** Searches for a metadata of given **access** token.
     * @return metadata or null if nothing was found */
    suspend fun findAccessToken(token: String): TokenMetadata?

    /** Searches for a metadata of given **refresh** token.
     * @return metadata or null if nothing was found */
    suspend fun findRefreshToken(token: String): TokenMetadata?

    /** Deletes given token and all its metadata. */
    suspend fun deleteToken(token: String)
}
