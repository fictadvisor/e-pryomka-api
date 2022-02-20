package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.domain.datasource.TokenDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.domain.interactors.AuthUseCaseImpl
import com.fictadvisor.pryomka.domain.models.toUserIdentifier
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.whenever
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiSecurityTest {
    private val entrant = entrant()
    private val operator = operator()
    private val admin = admin(id = "30b00eac-6e5b-4263-aef3-bde3140d7eb6".toUserIdentifier())

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

    private fun withProtectedTestApp(
        test: TestApplicationEngine.() -> Unit
    ) = withTestApplication({
        install(ContentNegotiation) { json() }
        configureSecurity(authUseCase)

        routing {
            authenticate(AUTH_GENERAL) {
                get("/general") { call.respond(HttpStatusCode.OK) }
            }

            authenticate(AUTH_ADMIN) {
                get("/admin") { call.respond(HttpStatusCode.OK) }
            }

            authenticate(AUTH_OPERATOR) {
                get("/operator") { call.respond(HttpStatusCode.OK) }
            }

            authenticate(AUTH_ENTRANT) {
                get("/entrant") { call.respond(HttpStatusCode.OK) }
            }

            get("/") { call.respond(HttpStatusCode.OK) }
        }
    }, test)

    @BeforeTest
    fun init(): Unit = runBlocking {
        whenever(userDataSource.findEntrant(entrant.id)).thenReturn(entrant)
        whenever(userDataSource.findEntrant(operator.id)).thenReturn(null)
        whenever(userDataSource.findEntrant(admin.id)).thenReturn(null)
        whenever(userDataSource.findStaff(entrant.id)).thenReturn(null)
        whenever(userDataSource.findStaff(operator.id)).thenReturn(operator)
        whenever(userDataSource.findStaff(admin.id)).thenReturn(admin)
        whenever(userDataSource.findStaffByCredentials("operator", "operator")).thenReturn(operator)
        whenever(userDataSource.findStaffByCredentials("admin", "admin")).thenReturn(admin)
    }

    @Test
    fun `test general routes are accessible without authentication`() {
        // WHEN
        withProtectedTestApp {
            handleRequest(HttpMethod.Get, "/").apply {
                // THEN
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

//    @Test
//    fun `test general routes are accessible for entrants`() = runBlocking {
//        // GIVEN
//        whenever(authUseCase.findUser(entrant.id)).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.OK, response.status())
//            }
//        }
//
//        return@runBlocking
//    }

    @Test
    fun `test general routes are accessible for operators`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("operator", "operator")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test general routes are accessible for admins`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("admin", "admin")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test general routes are not accessible for non-authenticated users`() = runBlocking {
        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general")

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

//    @Test
//    fun `test basic routes are accessible for entrants`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/general") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.OK, response.status())
//            }
//        }
//
//        return@runBlocking
//    }
//
//    @Test
//    fun `test basic routes create user if not found`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(null)
//        Mockito.`when`(createUserUseCase.invoke(any())).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/general") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.OK, response.status())
//                runBlocking {
//                    Mockito.verify(createUserUseCase, times(1)).invoke(any())
//                }
//            }
//        }
//
//        return@runBlocking
//    }
//
    @Test
    fun `test basic routes are accessible for operators`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("operator", "operator")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test basic routes are accessible for admins`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("admin", "admin")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

//    @Test
//    fun `test entrant routes are accessible for entrants`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/entrant") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.OK, response.status())
//            }
//        }
//
//        return@runBlocking
//    }
//
//    @Test
//    fun `test entrant routes create user if not found`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(null)
//        Mockito.`when`(createUserUseCase.invoke(any())).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/entrant") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.OK, response.status())
//                runBlocking {
//                    Mockito.verify(createUserUseCase, times(1)).invoke(any())
//                }
//            }
//        }
//
//        return@runBlocking
//    }

    @Test
    fun `test entrant routes are not accessible for operators`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("operator", "operator")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test entrant routes are not accessible for admin`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("admin", "admin")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }
//
//    @Test
//    fun `test operator routes are not accessible for entrants`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/entrant") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.Unauthorized, response.status())
//            }
//        }
//
//        return@runBlocking
//    }
//
    @Test
    fun `test operator routes are accessible for operators`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("operator", "operator")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/operator") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test operator routes are accessible for admins`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("admin", "admin")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/operator") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

//    @Test
//    fun `test admin routes are not accessible for entrants`() = runBlocking {
//        // GIVEN
//        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)
//
//        // WHEN
//        withProtectedTestApp {
//            val call = handleRequest(HttpMethod.Get, "/admin") {
//                addHeader(
//                    HttpHeaders.Authorization,
//                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
//                )
//            }
//
//            // THEN
//            with(call) {
//                assertEquals(HttpStatusCode.Unauthorized, response.status())
//            }
//        }
//
//        return@runBlocking
//    }

    @Test
    fun `test admin routes are not accessible for operators`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("operator", "operator")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/admin") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test admin routes are accessible for admins`() = runBlocking {
        // GIVEN
        val (accessToken, _) = authUseCase.logIn("admin", "admin")

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/admin") {
                addHeader(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }
}
