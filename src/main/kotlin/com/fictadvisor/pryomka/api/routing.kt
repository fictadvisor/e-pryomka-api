package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.CreateOperatorDto
import com.fictadvisor.pryomka.api.routes.*
import com.fictadvisor.pryomka.domain.models.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        authenticate(AUTH_GENERAL) {
            generalApplicationsRouters()
            meRoute()
        }

        authenticate(AUTH_ADMIN) {
            operatorsRoutes()
        }

        authenticate(AUTH_OPERATOR) {
            operatorApplicationsRouters()
        }

        authenticate(AUTH_ENTRANT) {
            myApplicationsRouters()
        }

        authRouters()

        get("/") {
            call.respondText("Welcome to FICT!")
        }

        post<CreateOperatorDto>("/register_admin") { (login, password) ->
            try {
                Provider.registerStaffUseCase.register(login, password, User.Staff.Role.Admin)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
