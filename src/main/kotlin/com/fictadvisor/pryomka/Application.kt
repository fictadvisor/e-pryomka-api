package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.api.configureRouting
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.api.configureServer
import com.fictadvisor.pryomka.data.db.configureDB
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Main application function. Creates server that listens on [Environment.HOST] on port [Environment.PORT].
 * Also, it initializes default options for handling requests and responses like CORS policy, JSON serialization
 * and security schemas. */
fun main() {
    embeddedServer(Netty, port = Environment.PORT, host = Environment.HOST) {
        configureDB()
        configureServer()
        configureDi()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}
