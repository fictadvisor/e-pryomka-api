package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.api.routes.applicationRouters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        authenticate(AUTH_OPERATOR) {
            get("/applications/all") {}
        }

        authenticate(AUTH_ENTRANT) {
            applicationRouters()
        }

        get("/") {
            call.respondText("Welcome to FICT!")
        }
    }
}
