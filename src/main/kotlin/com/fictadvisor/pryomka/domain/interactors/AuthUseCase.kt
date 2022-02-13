package com.fictadvisor.pryomka.domain.interactors

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.TokenMetadata
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import java.util.*
import kotlin.text.toByteArray

interface AuthUseCase {
    /** @return [Pair] where [Pair.first] is an access token and [Pair.second] is a refresh token */
    suspend fun logIn(login: String, password: String): Pair<String, String>
    /** @return [Pair] where [Pair.first] is an access token and [Pair.second] is a refresh token */
    suspend fun refresh(token: String): Pair<String, String>
    suspend fun auth(token: String): Boolean
    suspend fun findUser(userId: UserIdentifier): User?
    suspend fun getMe(token: String): User?
}

class AuthUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val tokenDataSource: TokenDataSource,
) : AuthUseCase {
    override suspend fun logIn(login: String, password: String): Pair<String, String> {
        val user = userDataSource.findStaffByCredentials(login, password) ?: error("User not found")
        return generateTokens(user.id)
    }

    override suspend fun refresh(token: String): Pair<String, String> {
        val (userId, validUntil) = tokenDataSource.findRefreshToken(token)
            ?: error("Token not found")

        tokenDataSource.deleteToken(token)

        if (!Date().before(validUntil)) error("Token expired")

        return generateTokens(userId)
    }

    override suspend fun auth(token: String): Boolean {
        val (_, validUntil) = tokenDataSource.findAccessToken(token) ?: error("Token not found")

        val tokenValid = Date().before(validUntil)

        if (!tokenValid) tokenDataSource.deleteToken(token)

        return tokenValid
    }

    override suspend fun findUser(userId: UserIdentifier): User? = with(userDataSource) {
        findEntrant(userId) ?: findStaff(userId)
    }

    override suspend fun getMe(token: String): User? {
        val (userId, validUntil) = tokenDataSource.findAccessToken(token)
            ?: error("Token not found")

        if (!Date().before(validUntil)) return null

        return findUser(userId)
    }

    private suspend fun generateTokens(userId: UserIdentifier): Pair<String, String> {
        val (accessToken, accessValidUntil) = generateAccessToken(userId)
        val (refreshToken, refreshValidUntil) = generateRefreshToken()

        tokenDataSource.saveToken(
            refreshToken,
            TokenMetadata(userId, refreshValidUntil, TokenMetadata.Type.Refresh)
        )
        tokenDataSource.saveToken(
            accessToken,
            TokenMetadata(userId, accessValidUntil, TokenMetadata.Type.Access)
        )

        return accessToken to refreshToken
    }

    private fun generateAccessToken(userId: UserIdentifier): Pair<String, Date> {
        val validUntil = Date(
            System.currentTimeMillis() + Environment.JWT_ACCESS_TOKEN_EXPIRATION_TIME
        )

        val token = JWT.create()
            .withAudience(Environment.JWT_AUDIENCE)
            .withIssuer(Environment.JWT_ISSUER)
            .withClaim("user_id", userId.value.toString())
            .withExpiresAt(validUntil)
            .sign(Algorithm.HMAC256(Environment.JWT_SECRET))

        return token to validUntil
    }

    private fun generateRefreshToken(): Pair<String, Date> {
        val header = UUID.randomUUID().toString().toByteArray()
        val payload = UUID.randomUUID().toString().toByteArray()

        val signed = Algorithm.HMAC256(Environment.JWT_SECRET).sign(header, payload)
        val validUntil = Date(
            System.currentTimeMillis() + Environment.JWT_REFRESH_TOKEN_EXPIRATION_TIME
        )

        return Base64.getEncoder().encodeToString(signed) to validUntil
    }
}
