package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.AUTH_GENERAL
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.api.dto.ChangeApplicationStatusDto
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCaseImpl
import com.fictadvisor.pryomka.domain.interactors.ChangeApplicationStatusUseCase
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneralApplicationEndpointsTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<AuthUseCase> { AuthUseCaseImpl(get(), get(), config) }
        })
    }

    private val config = AuthUseCase.Config(
        accessTTL = 600 * 1000L,
        refreshTTL = 5000 * 60 * 1000L,
        audience = "e-pryomka",
        issuer = "fictadvisor",
        secret = "9+FaLoftq7pK0mXiQf5IfH4tpYYJ6zutDfk28jSX5uQ=",
        realm = "vstup",
        tgBotId = "4002278938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp",
    )
    private val authUseCase: AuthUseCase by inject()
    private val admin = admin()

    private inline fun withGeneralApplicationsRouters(
        crossinline test: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication({
            install(ContentNegotiation) { json() }
            configureSecurity()
            routing {
                authenticate(AUTH_GENERAL) {
                    generalApplicationsRouters()
                }
            }
        }) {
            test()
        }
    }

    @BeforeTest
    fun init() {
        declareSuspendMock<UserDataSource> {
            whenever(findStaffByCredentials(any(), any())).thenReturn(admin)
            whenever(findStaff(any(), any())).thenReturn(admin)
        }

        declareMock<TokenDataSource> {}
        declareMock<ChangeApplicationStatusUseCase> {}
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

        declareSuspendMock<ChangeApplicationStatusUseCase> {
            whenever(
                changeStatus(id, admin.id, Application.Status.Approved, null)
            ).thenThrow(Unauthorized())
        }

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

        declareSuspendMock<ChangeApplicationStatusUseCase> {
            whenever(changeStatus(id, admin.id, Application.Status.Approved, null))
                .thenThrow(NotFound("Application not found"))
        }

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

        declareSuspendMock<ChangeApplicationStatusUseCase> {
            whenever(changeStatus(id, admin.id, Application.Status.Approved, null))
                .thenThrow(PermissionDenied("You have no rights to perform this action"))
        }

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
