package com.fictadvisor.pryomka.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.toUserIdentifierOrNull
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

const val AUTH_GENERAL = "auth_general"
const val AUTH_ENTRANT = "entrant"
const val AUTH_OPERATOR = "operator"
const val AUTH_ADMIN = "admin"

fun Application.configureSecurity(authUseCase: AuthUseCase) {
    fun jwt() = JWT.require(Algorithm.HMAC256(Environment.JWT_SECRET))
        .withAudience(Environment.JWT_AUDIENCE)
        .withIssuer(Environment.JWT_ISSUER)
        .build()

    suspend fun Payload.user() = getClaim("user_id").asString()
        .let(String::toUserIdentifierOrNull)
        ?.let { authUseCase.findUser(it) }

    install(Authentication) {
        jwt(AUTH_GENERAL) {
            realm = Environment.JWT_REALM
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_ADMIN) {
            realm = Environment.JWT_REALM
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.role == User.Role.Admin }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_OPERATOR) {
            realm = Environment.JWT_REALM
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.role == User.Role.Admin || it.role == User.Role.Operator }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }

        jwt(AUTH_ENTRANT) {
            realm = Environment.JWT_REALM
            verifier(jwt())

            validate { credential -> credential.payload
                .user()
                ?.takeIf { it.role == User.Role.Entrant }
                ?.let { JWTPrincipal(credential.payload) }
            }
        }
    }
}
