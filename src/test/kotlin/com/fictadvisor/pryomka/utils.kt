package com.fictadvisor.pryomka

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mockito.Mockito

internal inline fun <reified T> any(): T = Mockito.any(T::class.java)

internal inline fun withRouters(
    crossinline endpoints: Route.() -> Unit,
    crossinline test: TestApplicationEngine.() -> Unit
) {
    withTestApplication({
        install(ContentNegotiation) { json() }
        routing { endpoints() }
    }) {
        test()
    }
}

internal inline fun <reified T> TestApplicationResponse.body() = Json.decodeFromString<T>(
    content ?: error("Body is empty")
)

internal inline fun <reified T> TestApplicationRequest.setJsonBody(body: T) = setBody(
    Json.encodeToString(body)
)
