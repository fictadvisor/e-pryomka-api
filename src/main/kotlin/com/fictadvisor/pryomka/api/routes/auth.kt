package com.fictadvisor.pryomka.api.routes

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.auth() {
    static("auth") {
        file("", "auth.html")
        default("auth.html")
    }

    post("auth/telegram") {

    }
}