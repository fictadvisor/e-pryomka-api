package com.fictadvisor.pryomka

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.fictadvisor.pryomka.plugins.configureRouting
import com.fictadvisor.pryomka.plugins.configureSecurity
import com.fictadvisor.pryomka.plugins.initDB

fun main() {
    embeddedServer(Netty, port = Environment.PORT, host = Environment.HOST) {
        initDB()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
