package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Tokens
import com.fictadvisor.pryomka.data.encryption.Hash
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.models.TokenMetadata
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.*

class TokenDataSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TokenDataSource {
    override suspend fun saveToken(
        token: String,
        metadata: TokenMetadata,
    ): Unit = newSuspendedTransaction(dispatcher) {
        Tokens.insert {
            val salt = Hash.generateSalt()
            it[Tokens.userId] = metadata.userId.value
            it[Tokens.token] = Hash(token, salt)
            it[Tokens.salt] = salt
            it[Tokens.validUntil] = Instant.ofEpochMilli(metadata.validUntil.time)
            it[Tokens.type] = metadata.type
        }
    }

    override suspend fun findAccessToken(token: String): TokenMetadata? = findTokenByType(
        token,
        TokenMetadata.Type.Access
    )

    override suspend fun findRefreshToken(token: String): TokenMetadata? = findTokenByType(
        token,
        TokenMetadata.Type.Refresh
    )

    private suspend fun findTokenByType(
        token: String,
        type: TokenMetadata.Type
    ): TokenMetadata? = newSuspendedTransaction(dispatcher) {
        Tokens.selectAllBatched().forEach { rows ->
            rows.forEach {
                val hash = it[Tokens.token]
                val salt = it[Tokens.salt]
                val tokenType = it[Tokens.type]

                if (tokenType == type && Hash.verify(token, hash, salt)) {
                    val id = it[Tokens.userId]
                    val timestamp = it[Tokens.validUntil].toEpochMilli()

                    return@newSuspendedTransaction TokenMetadata(
                        userId = UserIdentifier(id),
                        validUntil = Date(timestamp),
                        type = tokenType
                    )
                }
            }
        }
        null
    }

    override suspend fun deleteToken(token: String): Unit = newSuspendedTransaction(dispatcher) {
        Tokens.selectAllBatched().forEach { rows ->
            rows.forEach {
                val hash = it[Tokens.token]
                val salt = it[Tokens.salt]

                if (Hash.verify(token, hash, salt)) {
                    return@newSuspendedTransaction newSuspendedTransaction(dispatcher) {
                        Tokens.deleteWhere {
                            Tokens.token.eq(hash).and {
                                Tokens.salt eq salt
                            }
                        }
                    }
                }
            }
        }
    }
}
