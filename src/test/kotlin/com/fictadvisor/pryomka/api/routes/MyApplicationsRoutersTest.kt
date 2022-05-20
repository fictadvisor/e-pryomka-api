package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.AUTH_ENTRANT
import com.fictadvisor.pryomka.api.configureSecurity
import com.fictadvisor.pryomka.api.dto.ApplicationListDto
import com.fictadvisor.pryomka.api.dto.ApplicationRequestDto
import com.fictadvisor.pryomka.api.dto.faculty.LearningFormatDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.ApplicationUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCaseImpl
import com.fictadvisor.pryomka.domain.interactors.SubmitDocumentUseCase
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.Duplicated
import com.fictadvisor.pryomka.domain.models.generateLearningFormatId
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

class MyApplicationsRoutersTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<ApplicationUseCase> { declareMock() }
            single<SubmitDocumentUseCase> { declareMock() }
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
    private val entrant = entrant()
    private val telegramData = telegramData(id = entrant.telegramId, tgBotId = config.tgBotId)
    private val speciality = SpecialityDto(121, "Software Engineering")
    private val learningFormat = LearningFormatDto(generateLearningFormatId().value.toString(), "Full time studying")

    private inline fun withMyApplicationsRouters(
        crossinline test: TestApplicationEngine.() -> Unit
    ) {
        withTestApplication({
            install(ContentNegotiation) { json() }
            configureSecurity()
            routing {
                authenticate(AUTH_ENTRANT) {
                    myApplicationsRouters()
                }
            }
        }) {
            test()
        }
    }

    @BeforeTest
    fun init() {
        declareSuspendMock<TokenDataSource> {
            whenever(saveToken(any(), any())).thenReturn(1)
        }

        declareSuspendMock<UserDataSource> {
            whenever(findEntrant(entrant.id)).thenReturn(entrant)
            whenever(findEntrantByTelegramId(entrant.telegramId)).thenReturn(entrant)
        }
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

        declareSuspendMock<ApplicationUseCase> {
            whenever(getByUserId(entrant.id)).thenReturn(applications)
        }

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

        declareSuspendMock<ApplicationUseCase> {
            whenever(getByUserId(entrant.id)).thenReturn(listOf())
        }

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
            speciality = speciality,
            funding = Application.Funding.Budget,
            learningFormat = learningFormat,
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
            speciality = speciality,
            funding = Application.Funding.Budget,
            learningFormat = learningFormat,
        )

        declareSuspendMock<ApplicationUseCase> {
            whenever(create(any(), any())).thenThrow(Duplicated("Application already exists"))
        }

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
