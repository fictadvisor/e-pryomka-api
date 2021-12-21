package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.api.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.data.db.initDB
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun main() {
    embeddedServer(Netty, port = Environment.PORT, host = Environment.HOST) {
        initDB()
        install(ContentNegotiation) { json() }
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
