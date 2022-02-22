package com.fictadvisor.pryomka.domain.interactors

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.data.encryption.Hash
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.mappers.toEntrant
import com.fictadvisor.pryomka.domain.models.*
import java.util.*
import kotlin.text.toByteArray

interface AuthUseCase {
    val config: Config

    /** @return [Pair] where [Pair.first] is an access token and [Pair.second] is a refresh token */
    suspend fun logIn(login: String, password: String): Pair<String, String>

    /** @return [Pair] where [Pair.first] is an access token and [Pair.second] is a refresh token */
    suspend fun refresh(token: String): Pair<String, String>

    suspend fun auth(token: String): Boolean

    suspend fun findUser(userId: UserIdentifier): User?

    suspend fun getMe(token: String): User?

    /** Exchanges Telegram authentication token, obtained via bot to
     * our refresh/access token pair. If user with given Telegram id does not exist,
     * it will be registered. For now, Telegram authentication is available only to
     * entrants.
     *
     * @return [Pair] where [Pair.first] is an access token and [Pair.second] is a refresh token */
    suspend fun exchange(telegramData: TelegramData): Pair<String, String>

    data class Config(
        val accessTTL: Long,
        val refreshTTL: Long,
        val audience: String,
        val issuer: String,
        val secret: String,
        val realm: String,
        val tgBotId: String,
    ) {
        companion object {
            val DEFAULT get() = Config(
                accessTTL = Environment.JWT_ACCESS_TOKEN_EXPIRATION_TIME,
                refreshTTL = Environment.JWT_REFRESH_TOKEN_EXPIRATION_TIME,
                audience = Environment.JWT_AUDIENCE,
                issuer = Environment.JWT_ISSUER,
                secret = Environment.JWT_SECRET,
                realm = Environment.JWT_REALM,
                tgBotId = Environment.TG_BOT_ID,
            )
        }
    }
}

class AuthUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val tokenDataSource: TokenDataSource,
    override val config: AuthUseCase.Config = AuthUseCase.Config.DEFAULT
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
        val (_, validUntil) = tokenDataSource.findAccessToken(token) ?: return false

        val tokenValid = Date().before(validUntil)

        if (!tokenValid) tokenDataSource.deleteToken(token)

        return tokenValid
    }

    override suspend fun findUser(userId: UserIdentifier): User? = with(userDataSource) {
        findEntrant(userId) ?: findStaff(userId)
    }

    override suspend fun getMe(token: String): User? {
        val (userId, validUntil) = tokenDataSource.findAccessToken(token) ?: return null

        if (!Date().before(validUntil)) return null

        return findUser(userId)
    }

    override suspend fun exchange(telegramData: TelegramData): Pair<String, String> {
        require(Hash.verifyTelegramData(telegramData, config.tgBotId))

        var entrant = telegramData.toEntrant()
        val existing = userDataSource.findEntrantByTelegramId(entrant.telegramId)

        if (existing != null) {
            userDataSource.updateEntrant(entrant)
            entrant = existing
        } else {
            userDataSource.registerEntrant(entrant)
        }

        return generateTokens(entrant.id)
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
            System.currentTimeMillis() + config.accessTTL
        )

        val token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("user_id", userId.value.toString())
            .withExpiresAt(validUntil)
            .sign(Algorithm.HMAC256(config.secret))

        return token to validUntil
    }

    private fun generateRefreshToken(): Pair<String, Date> {
        val header = UUID.randomUUID().toString().toByteArray()
        val payload = UUID.randomUUID().toString().toByteArray()

        val signed = Algorithm.HMAC256(config.secret).sign(header, payload)
        val validUntil = Date(
            System.currentTimeMillis() + config.refreshTTL
        )

        return Base64.getEncoder().encodeToString(signed) to validUntil
    }
}
