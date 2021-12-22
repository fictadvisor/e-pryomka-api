package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.api.routes.auth
import com.fictadvisor.pryomka.api.routes.myApplicationRouters
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        authenticate(AUTH_OPERATOR) {
            get("/applications/all") {}
        }

        authenticate(AUTH_ENTRANT) {
            myApplicationRouters()
        }

        get("/") {
            call.respondText("Welcome to FICT!")
        }

        static("auth") {
            default("./src/main/kotlin/com/fictadvisor/pryomka/auth.html")
        }
    }
}
