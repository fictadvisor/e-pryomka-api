package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.AUTH_ENTRANT
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.api.dto.ApplicationListDto
import com.fictadvisor.pryomka.api.dto.ApplicationRequestDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.ApplicationUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCaseImpl
import com.fictadvisor.pryomka.domain.interactors.SubmitDocumentUseCase
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.Duplicated
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.whenever
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MyApplicationsRoutersTest {
    private val applicationUseCase: ApplicationUseCase = mock()
    private val submitDocumentUseCase: SubmitDocumentUseCase = mock()
    private val userDataSource: UserDataSource = mock()
    private val tokenDataSource: TokenDataSource = mock()
    private val config = AuthUseCase.Config(
        accessTTL = 600 * 1000L,
        refreshTTL = 5000 * 60 * 1000L,
        audience = "e-pryomka",
        issuer = "fictadvisor",
        secret = "9+FaLoftq7pK0mXiQf5IfH4tpYYJ6zutDfk28jSX5uQ=",
        realm = "vstup",
        tgBotId = "4002278938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp",
    )
    private val authUseCase: AuthUseCase = AuthUseCaseImpl(userDataSource, tokenDataSource, config)
    private val entrant = entrant()
    private val telegramData = telegramData(id = entrant.telegramId, tgBotId = config.tgBotId)

    private inline fun withMyApplicationsRouters(
        crossinline test: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication({
            install(ContentNegotiation) { json() }
            routing {
                configureSecurity(authUseCase)

                authenticate(AUTH_ENTRANT) {
                    myApplicationsRouters(applicationUseCase, submitDocumentUseCase)
                }
            }
        }) {
            test()
        }
    }

    @BeforeTest
    fun init(): Unit = runBlocking {
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)
        whenever(userDataSource.findEntrantByTelegramId(entrant.telegramId)).thenReturn(entrant)
    }

    @Test
    fun `test GET my applications`() = runBlocking {
        // GIVEN
        val (token, _) = authUseCase.exchange(telegramData)
        val applications = listOf(
            application(),
            application(),
            application(),
        )

        val dto = ApplicationListDto(applications.map { it.toDto() })

        whenever(applicationUseCase.getByUserId(entrant.id)).thenReturn(applications)

        // WHEN + THEN
        withMyApplicationsRouters {
            val call = handleRequest(HttpMethod.Get, "/applications/my") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(dto, response.body())
            }
        }
    }

    @Test
    fun `test GET my applications - empty list`() = runBlocking {
        // GIVEN
        val (token, _) = authUseCase.exchange(telegramData)
        val dto = ApplicationListDto(listOf())

        whenever(applicationUseCase.getByUserId(entrant.id)).thenReturn(listOf())

        // WHEN + THEN
        withMyApplicationsRouters {
            val call = handleRequest(HttpMethod.Get, "/applications/my") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(dto, response.body())
            }
        }
    }

    @Test
    fun `test POST my application`() = runBlocking {
        // GIVEN
        val (token, _) = authUseCase.exchange(telegramData)
        val application = ApplicationRequestDto(
            speciality = Application.Speciality.SPEC_121,
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
        )

        // WHEN + THEN
        withMyApplicationsRouters {
            val call = handleRequest(HttpMethod.Post, "/applications/my") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(application)
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test POST my application - duplicate`() = runBlocking {
        // GIVEN
        val (token, _) = authUseCase.exchange(telegramData)
        val application = ApplicationRequestDto(
            speciality = Application.Speciality.SPEC_121,
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
        )

        whenever(applicationUseCase.create(any(), any()))
            .thenThrow(Duplicated("Application already exists"))

        // WHEN + THEN
        withMyApplicationsRouters {
            val call = handleRequest(HttpMethod.Post, "/applications/my") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(application)
            }

            with(call) {
                assertEquals(HttpStatusCode.Conflict, response.status())
                assertEquals("Application already exists", response.content)
            }
        }
    }

    @Test
    fun `test POST my application document - invalid id`() = runBlocking {
        // GIVEN
        val (token, _) = authUseCase.exchange(telegramData)

        // WHEN + THEN
        withMyApplicationsRouters {
            val call = handleRequest(HttpMethod.Post, "/applications/lelouch/documents") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Invalid application id", response.content)
            }
        }
    }
}
