package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.mappers.toWhoAmIDto
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.whenever
import com.fictadvisor.pryomka.withRouters
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MeRouteTest {
    private val useCase: AuthUseCase = mock()

    @Test
    fun `test GET me - unauthorized`() = runBlocking {
        // WHEN + THEN
        withRouters({ meRoute(useCase) }) {
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
        withRouters({ meRoute(useCase) }) {
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
        whenever(useCase.getMe(token)).thenReturn(null)

        // WHEN
        withRouters({ meRoute(useCase) }) {
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
        whenever(useCase.getMe(token)).thenThrow(IllegalStateException())

        // WHEN
        withRouters({ meRoute(useCase) }) {
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
        whenever(useCase.getMe(token)).thenReturn(entrant)

        // WHEN
        withRouters({ meRoute(useCase) }) {
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
        whenever(useCase.getMe(token)).thenReturn(operator)

        // WHEN
        withRouters({ meRoute(useCase) }) {
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
