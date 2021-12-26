package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.domain.interactors.CreateUserUseCase
import com.fictadvisor.pryomka.domain.interactors.FindUserUseCase
import com.fictadvisor.pryomka.domain.models.User
import io.ktor.application.*
import io.ktor.auth.*

const val AUTH_GENERAL = "auth_general"
const val AUTH_ENTRANT = "entrant"
const val AUTH_OPERATOR = "operator"
const val AUTH_ADMIN = "admin"

fun Application.configureSecurity(
    findUserUseCase: FindUserUseCase = Provider.findUserUseCase,
    createUserUseCase: CreateUserUseCase = Provider.createUserUseCase,
) {
    install(Authentication) {
        basic(AUTH_GENERAL) {
            validate { credentials ->
                val user = findUserUseCase.findByName(credentials.name)
                    ?: createUserUseCase(credentials.name)

                UserIdPrincipal(user.id.value.toString())
            }
        }

        basic(AUTH_ENTRANT) {
            validate { credentials ->
                val user = findUserUseCase.findByName(credentials.name)
                    ?: createUserUseCase(credentials.name)

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Entrant
                }
            }
        }

        basic(AUTH_OPERATOR) {
            validate { credentials ->
                val user = findUserUseCase.findByName(credentials.name) ?: return@validate null

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Operator || user.role == User.Role.Admin
                }
            }
        }

        basic(AUTH_ADMIN) {
            validate { credentials ->
                val user = findUserUseCase.findByName(credentials.name) ?: return@validate null

                UserIdPrincipal(user.id.value.toString()).takeIf {
                    user.role == User.Role.Admin
                }
            }
        }
    }
}
