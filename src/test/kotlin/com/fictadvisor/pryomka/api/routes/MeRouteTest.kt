package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.mappers.toWhoAmIDto
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertEquals

class MeRouteTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<AuthUseCase> { declareMock() }
        })
    }

    @Test
    fun `test GET me - unauthorized`() = runBlocking {
        // WHEN + THEN
        withRouters({ meRoute() }) {
            handleRequest(HttpMethod.Get, "/me").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test GET me - not bearer token`() = runBlocking {
        // GIVEN
        val authorizationHeader = "Basic 123456"

        // WHEN
        withRouters({ meRoute() }) {
            val call = handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, authorizationHeader)
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test GET me - user not found`() = runBlocking {
        // GIVEN
        val token = "123456"
        declareSuspendMock<AuthUseCase> { whenever(getMe(token)).thenReturn(null) }

        // WHEN
        withRouters({ meRoute() }) {
            val call = handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test GET me - exception thrown`() = runBlocking {
        // GIVEN
        val token = "123456"
        declareSuspendMock<AuthUseCase> { whenever(getMe(token)).thenThrow(IllegalStateException()) }

        // WHEN
        withRouters({ meRoute() }) {
            val call = handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test GET me - entrant dto`() = runBlocking {
        // GIVEN
        val token = "123456"
        val entrant = entrant()
        declareSuspendMock<AuthUseCase> { whenever(getMe(token)).thenReturn(entrant) }

        // WHEN
        withRouters({ meRoute() }) {
            val call = handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(entrant.toWhoAmIDto(), response.body())
            }
        }
    }

    @Test
    fun `test GET me - operator dto`() = runBlocking {
        // GIVEN
        val token = "123456"
        val operator = operator()
        declareSuspendMock<AuthUseCase> { whenever(getMe(token)).thenReturn(operator) }

        // WHEN
        withRouters({ meRoute() }) {
            val call = handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(operator.toWhoAmIDto(), response.body())
            }
        }
    }
}
