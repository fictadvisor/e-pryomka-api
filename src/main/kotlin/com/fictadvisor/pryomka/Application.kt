package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.api.configureRouting
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.data.db.initDB
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Main application function. Creates server that listens on [Environment.HOST] on port [Environment.PORT].
 * Also, it initializes default options for handling requests and responses like CORS policy, JSON serialization
 * and security schemas. */
fun main() {
    embeddedServer(Netty, port = Environment.PORT, host = Environment.HOST) {
        initDB()
        install(ContentNegotiation) { json() }
        install(DefaultHeaders)
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            header(HttpHeaders.Authorization)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
            anyHost() // TODO: Reconsider
        }
        configureSecurity(Provider.authUseCase)
        configureRouting()
    }.start(wait = true)
}
