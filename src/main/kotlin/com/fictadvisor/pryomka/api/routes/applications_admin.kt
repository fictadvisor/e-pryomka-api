package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import io.ktor.routing.*

fun Route.adminApplicationsRouters() {
    get("/operators") {
        val operators = Provider.findUserUseCase
    }

    post("/operators") {

    }

    delete("/operators/{id}") {

    }
}
