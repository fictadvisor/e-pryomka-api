package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.Provider
import io.ktor.auth.*
import io.ktor.application.*

const val AUTH_ENTRANT = "entrant"
const val AUTH_OPERATOR = "operator"
const val AUTH_ADMIN = "admin"

fun Application.configureSecurity() {
    install(Authentication) {
        basic(AUTH_ENTRANT) {
            validate { credentials ->
                val user = Provider.findUserUseCase.findByName(credentials.name)
                    ?: Provider.createUserUseCase(credentials.name)

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Entrant
                }
            }
        }

        basic(AUTH_OPERATOR) {
            validate { credentials ->
                val user = Provider.findUserUseCase.findByName(credentials.name)
                    ?: return@validate null

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Operator || user.role == User.Role.Admin
                }
            }
        }

        basic(AUTH_ADMIN) {
            validate { credentials ->
                val user = Provider.findUserUseCase.findByName(credentials.name)
                    ?: return@validate null

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Admin
                }
            }
        }
    }
}
