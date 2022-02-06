package com.fictadvisor.pryomka.domain.interactors

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import java.util.*

interface AuthUseCase {
    suspend fun logIn(login: String, password: String): String
    suspend fun findUser(userId: UserIdentifier): User?
}

class AuthUseCaseImpl(
    private val userDataSource: UserDataSource
) : AuthUseCase {
    override suspend fun logIn(login: String, password: String): String {
        val user = userDataSource.findStaffByCredentials(login, password) ?: error("User not found")

        return JWT.create()
            .withAudience(Environment.JWT_AUDIENCE)
            .withIssuer(Environment.JWT_ISSUER)
            .withClaim("user_id", user.id.value.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + Environment.JWT_EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(Environment.JWT_SECRET))
    }

    override suspend fun findUser(userId: UserIdentifier): User? = with(userDataSource) {
        findEntrant(userId) ?: findStaff(userId)
    }
}
