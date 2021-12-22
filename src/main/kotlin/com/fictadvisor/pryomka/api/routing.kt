package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.api.routes.myApplicationsRouters
import com.fictadvisor.pryomka.api.routes.operatorApplicationsRouters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        authenticate(AUTH_ADMIN) {
            get("/operators") {}
            post("/operators") {}
        }

        authenticate(AUTH_OPERATOR) {
            operatorApplicationsRouters()
        }

        authenticate(AUTH_ENTRANT) {
            put("/applicatoins/my") {}

            myApplicationsRouters()
        }

        get("/") {
            call.respondText("Welcome to FICT!")
        }
    }
}
