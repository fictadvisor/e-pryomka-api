package com.fictadvisor.pryomka.api

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.interactors.CreateUserUseCase
import com.fictadvisor.pryomka.domain.interactors.FindUserUseCase
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId
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
import org.mockito.Mockito.times
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiSecurityTest {
    private val createUserUseCase = Mockito.mock(CreateUserUseCase::class.java)
    private val findUserUseCase = Mockito.mock(FindUserUseCase::class.java)

    private val entrant = User(
        id = generateUserId(),
        name = "Lulu",
        role = User.Role.Entrant,
    )

    private val operator = User(
        id = generateUserId(),
        name = "Zero",
        role = User.Role.Operator,
    )

    private val admin = User(
        id = generateUserId(),
        name = "Lelouch vi Britannia",
        role = User.Role.Admin,
    )

    private fun withProtectedTestApp(
        test: TestApplicationEngine.() -> Unit
    ) = withTestApplication({
        install(ContentNegotiation) { json() }
        configureSecurity(findUserUseCase, createUserUseCase)

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

    @Test
    fun `test general routes are accessible for entrants`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test general routes are accessible for operators`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Zero".toByteArray())
                )
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
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(admin)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder()
                        .encodeToString("Lelouch vi Britannia".toByteArray())
                )
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

    @Test
    fun `test basic routes are accessible for entrants`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test basic routes create user if not found`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(null)
        Mockito.`when`(createUserUseCase.invoke(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                runBlocking {
                    Mockito.verify(createUserUseCase, times(1)).invoke(any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test basic routes are accessible for operators`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
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
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(admin)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/general") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test entrant routes are accessible for entrants`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test entrant routes create user if not found`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(null)
        Mockito.`when`(createUserUseCase.invoke(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                runBlocking {
                    Mockito.verify(createUserUseCase, times(1)).invoke(any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test entrant routes are not accessible for operators`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
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
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(admin)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test operator routes are not accessible for entrants`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/entrant") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test operator routes are accessible for operators`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/operator") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
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
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(admin)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/operator") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test admin routes are not accessible for entrants`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(entrant)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/admin") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test admin routes are not accessible for operators`() = runBlocking {
        // GIVEN
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(operator)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/admin") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
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
        Mockito.`when`(findUserUseCase.findByName(any())).thenReturn(admin)

        // WHEN
        withProtectedTestApp {
            val call = handleRequest(HttpMethod.Get, "/admin") {
                addHeader(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("Lulu:Britannia".toByteArray())
                )
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }
}
