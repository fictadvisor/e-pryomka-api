package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.AUTH_GENERAL
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.api.dto.ChangeApplicationStatusDto
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.*
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
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

class GeneralApplicationEndpointsTest {
    private val useCase: ChangeApplicationStatusUseCase = mock()
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
    private val admin = admin()

    private inline fun withGeneralApplicationsRouters(
        crossinline test: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication({
            install(ContentNegotiation) { json() }
            routing {
                configureSecurity(authUseCase)

                authenticate(AUTH_GENERAL) {
                    generalApplicationsRouters(useCase)
                }
            }
        }) {
            test()
        }
    }

    @BeforeTest
    fun init(): Unit = runBlocking {
        whenever(userDataSource.findStaffByCredentials(any(), any())).thenReturn(admin)
        whenever(userDataSource.findStaff(any(), any())).thenReturn(admin)
    }

    @Test
    fun `test PUT application`() = runBlocking {
        // GIVEN
        val id = generateApplicationId().value
        val (token) = authUseCase.logIn("admin", "admin")
        val changeStatusDto = ChangeApplicationStatusDto(Application.Status.Approved)

        // WHEN + THEN
        withGeneralApplicationsRouters {
            val call = handleRequest(HttpMethod.Put, "/applications/$id") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(changeStatusDto)
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test PUT application - invalid id`() = runBlocking {
        // GIVEN
        val (token) = authUseCase.logIn("admin", "admin")
        val changeStatusDto = ChangeApplicationStatusDto(Application.Status.Approved)

        // WHEN + THEN
        withGeneralApplicationsRouters {
            val call = handleRequest(HttpMethod.Put, "/applications/lelouch") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(changeStatusDto)
            }

            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Invalid application id", response.content)
            }
        }
    }

    @Test
    fun `test PUT application - unauthorized`() = runBlocking {
        // GIVEN
        val id = generateApplicationId()
        val (token) = authUseCase.logIn("admin", "admin")
        val changeStatusDto = ChangeApplicationStatusDto(Application.Status.Approved)

        whenever(useCase.changeStatus(id, admin.id, Application.Status.Approved, null))
            .thenThrow(Unauthorized())

        // WHEN + THEN
        withGeneralApplicationsRouters {
            val call = handleRequest(HttpMethod.Put, "/applications/${id.value}") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(changeStatusDto)
            }

            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test PUT application - not found`() = runBlocking {
        // GIVEN
        val id = generateApplicationId()
        val (token) = authUseCase.logIn("admin", "admin")
        val changeStatusDto = ChangeApplicationStatusDto(Application.Status.Approved)

        whenever(useCase.changeStatus(id, admin.id, Application.Status.Approved, null))
            .thenThrow(NotFound("Application not found"))

        // WHEN + THEN
        withGeneralApplicationsRouters {
            val call = handleRequest(HttpMethod.Put, "/applications/${id.value}") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(changeStatusDto)
            }

            with(call) {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Application not found", response.content)
            }
        }
    }

    @Test
    fun `test PUT application - permission denied`() = runBlocking {
        // GIVEN
        val id = generateApplicationId()
        val (token) = authUseCase.logIn("admin", "admin")
        val changeStatusDto = ChangeApplicationStatusDto(Application.Status.Approved)

        whenever(useCase.changeStatus(id, admin.id, Application.Status.Approved, null))
            .thenThrow(PermissionDenied("You have no rights to perform this action"))

        // WHEN + THEN
        withGeneralApplicationsRouters {
            val call = handleRequest(HttpMethod.Put, "/applications/${id.value}") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
                setJsonBody(changeStatusDto)
            }

            with(call) {
                assertEquals(HttpStatusCode.Forbidden, response.status())
                assertEquals("You have no rights to perform this action", response.content)
            }
        }
    }
}
