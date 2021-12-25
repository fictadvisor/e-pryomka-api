package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.api.routes.adminApplicationsRouters
import com.fictadvisor.pryomka.api.routes.generalApplicationsRouters
import com.fictadvisor.pryomka.api.routes.myApplicationsRouters
import com.fictadvisor.pryomka.api.routes.operatorApplicationsRouters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        authenticate(AUTH_GENERAL) {
            generalApplicationsRouters()
        }

        authenticate(AUTH_ADMIN) {
            adminApplicationsRouters()
        }

        authenticate(AUTH_OPERATOR) {
            operatorApplicationsRouters()
        }

        authenticate(AUTH_ENTRANT) {
            myApplicationsRouters()
        }

        get("/") {
            call.respondText("Welcome to FICT!")
        }
    }
}
