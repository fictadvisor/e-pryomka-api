package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import io.ktor.auth.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.sessions.*
import java.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserIdPrincipal>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }
    install(Authentication) {
        basic("auth-basic") {
            validate { credentials ->
                val user = Provider.findUserUseCase.findByName(credentials.name)
                    ?: Provider.createUserUseCase(credentials.name)

                println("a1")
                sessions.set(user)
                UserIdPrincipal(user.id.value.toString())
            }
        }

        session<User>("auth-session") {
            validate { user ->
                println("a3")
                UserIdPrincipal(user.id.value.toString())
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
