package com.fictadvisor.pryomka.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.models.toUserIdentifierOrNull
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.koin.ktor.ext.inject

const val AUTH_GENERAL = "auth_general"
const val AUTH_ENTRANT = "entrant"
const val AUTH_OPERATOR = "operator"
const val AUTH_ADMIN = "admin"

fun Application.configureSecurity() {
    val authUseCase: AuthUseCase by inject()

    fun jwt() = JWT.require(Algorithm.HMAC256(authUseCase.config.secret))
        .withAudience(authUseCase.config.audience)
        .withIssuer(authUseCase.config.issuer)
        .build()

    suspend fun Payload.user() = getClaim("user_id").asString()
        .let(String::toUserIdentifierOrNull)
        ?.let { authUseCase.findUser(it) }

    install(Authentication) {
        jwt(AUTH_GENERAL) {
            realm = authUseCase.config.realm
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_ADMIN) {
            realm = authUseCase.config.realm
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.isAdmin }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_OPERATOR) {
            realm = authUseCase.config.realm
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.isOperator || it.isAdmin }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_ENTRANT) {
            realm = authUseCase.config.realm
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.isEntrant }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }
    }
}
