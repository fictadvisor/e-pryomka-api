package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.TokenMetadata

interface TokenDataSource {
    suspend fun saveToken(token: String, metadata: TokenMetadata)
    suspend fun findAccessToken(token: String): TokenMetadata?
    suspend fun findRefreshToken(token: String): TokenMetadata?
    suspend fun deleteToken(token: String)
}
