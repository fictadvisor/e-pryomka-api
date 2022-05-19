package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.encryption.Hash
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.qualifier.Qualifier
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import kotlin.reflect.KClass

internal inline fun <reified T> any(): T = Mockito.any(T::class.java)
internal inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
internal fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
internal fun <T> verify(mock: T, times: Int = 1) = Mockito.verify(mock, Mockito.times(times))

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

internal inline fun <reified T> TestApplicationRequest.setJsonBody(body: T) {
    addHeader(HttpHeaders.ContentType, "application/json")
    setBody(Json.encodeToString(body))
}

fun entrant(
    id: UserIdentifier = generateUserId(),
    telegramId: Long = 1,
    firstName: String = "Lelouch",
    lastName: String? = "Lamperouge",
    userName: String? = "lelouch",
    photoUrl: String? = "http://photos.com/lelouch"
) = User.Entrant(id, telegramId, firstName, lastName, userName, photoUrl)

fun operator(
    id: UserIdentifier = generateUserId(),
    name: String = "lelouch",
) = User.Staff(id, name, User.Staff.Role.Operator)

fun admin(
    id: UserIdentifier = generateUserId(),
    name: String = "lelouch",
) = User.Staff(id, name, User.Staff.Role.Admin)

fun application(
    id: ApplicationIdentifier = generateApplicationId(),
    userId: UserIdentifier = generateUserId(),
    documents: Set<DocumentType> = setOf(),
    funding: Application.Funding = Application.Funding.Budget,
    speciality: Speciality = Speciality(121, "Software Engineering"),
    learningFormat: LearningFormat = LearningFormat(generateLearningFormatId(), "Full time studying"),
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
    statusMessage = statusMsg,
)

fun telegramData(
    authDate: Long = 123456,
    id: Long = 1234567890L,
    firstName: String = "Lelouch",
    lastName: String? = "Lamperouge",
    userName: String? = "lelouch",
    photoUrl: String? = "http://photos.com/lelouch",
    tgBotId: String? = null,
): TelegramData {
    var data = mutableMapOf(
        "auth_date" to JsonPrimitive(authDate),
        "id" to JsonPrimitive(id),
        "first_name" to JsonPrimitive(firstName),
        "last_name" to JsonPrimitive(lastName),
        "username" to JsonPrimitive(userName),
        "photo_url" to JsonPrimitive(photoUrl),
    )

    tgBotId?.let {
        val hash = Hash.hashTelegramData(data, it)
        data += "hash" to JsonPrimitive(hash)
    }

    return data
}

fun telegramDataDto(
    tgBotId: String,
    authDate: Long = 123456,
    id: Long = 1234567890L,
    firstName: String = "Lelouch",
    lastName: String? = "Lamperouge",
    userName: String? = "lelouch",
    photoUrl: String? = "http://photos.com/lelouch",
): String {
    val data = telegramData(authDate, id, firstName, lastName, userName, photoUrl, tgBotId)
    return Json.encodeToString(data)
}

inline fun <reified T : Any> KoinTest.declareSuspendMock(
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    crossinline stubbing: suspend T.() -> Unit = {}
): T = declareMock(qualifier, secondaryTypes) {
    runBlocking { stubbing() }
}
