package com.fictadvisor.pryomka.plugins

import io.ktor.auth.*
import io.ktor.application.*

fun Application.configureSecurity() {
    install(Authentication) {
        basic {
            validate { credentials ->
                val user = Provider.findUserUseCase.findByName(credentials.name)
                    ?: Provider.createUserUseCase(credentials.name)

                UserIdPrincipal(user.id.value.toString())
            }
        }
    }
}
