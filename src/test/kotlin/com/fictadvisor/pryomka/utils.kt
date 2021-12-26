package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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

fun user(
    id: UserIdentifier = generateUserId(),
    name: String = "",
    role: User.Role = User.Role.Entrant,
) = User(id, name, role)

fun application(
    id: ApplicationIdentifier = generateApplicationId(),
    userId: UserIdentifier = generateUserId(),
    documents: Set<DocumentType> = setOf(),
    funding: Application.Funding = Application.Funding.Budget,
    speciality: Application.Speciality = Application.Speciality.SPEC_121,
    learningFormat: Application.LearningFormat = Application.LearningFormat.FullTime,
    createdAt: Instant = Clock.System.now(),
    status: Application.Status = Application.Status.Pending,
    statusMsg: String? = null,
) = Application(
    id = id,
    userId = userId,
    documents = documents,
    funding = funding,
    speciality = speciality,
    learningFormat = learningFormat,
    createdAt = createdAt,
    status = status,
    statusMsg = statusMsg,
)