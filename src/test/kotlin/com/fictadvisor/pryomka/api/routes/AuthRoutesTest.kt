package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.dto.LogInRequestDto
import com.fictadvisor.pryomka.api.dto.LogInResponseDto
import com.fictadvisor.pryomka.api.dto.RefreshRequest
import com.fictadvisor.pryomka.api.mappers.toTelegramData
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRoutesTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    val koinTestRule = KoinTestRule.create {}

    private val login = "lelouch"
    private val password = "lamperouge"
    private val tgBotId = "4002278938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp"

    @Test
    fun `test POST login`() = runBlocking {
        // GIVEN
        val resp = LogInResponseDto("1234", "5678")

        declareSuspendMock<AuthUseCase> {
            whenever(logIn(login, password)).thenReturn(
                resp.access to resp.refresh
            )
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/login") {
                setJsonBody(LogInRequestDto(login, password))
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(resp, response.body())
            }
        }
    }

    @Test
    fun `test POST login - error`() = runBlocking {
        // GIVEN
        declareSuspendMock<AuthUseCase> {
            whenever(logIn(login, password)).thenThrow(IllegalStateException())
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/login") {
                setJsonBody(LogInRequestDto(login, password))
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test POST refresh`() = runBlocking {
        // GIVEN
        val token = "abcd"
        val resp = LogInResponseDto("1234", "5678")

        declareSuspendMock<AuthUseCase> {
            whenever(refresh(token)).thenReturn(resp.access to resp.refresh)
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/refresh") {
                setJsonBody(RefreshRequest(token))
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(resp, response.body())
            }
        }
    }

    @Test
    fun `test POST refresh - error`() = runBlocking {
        // GIVEN
        declareSuspendMock<AuthUseCase> {
            whenever(refresh(any())).thenThrow(IllegalStateException())
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/refresh") {
                setJsonBody(RefreshRequest("abcd"))
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `test POST exchange`() = runBlocking {
        // GIVEN
        val data = telegramDataDto(tgBotId)
        val resp = LogInResponseDto("1234", "5678")

        declareSuspendMock<AuthUseCase> {
            whenever(exchange(data.toTelegramData())).thenReturn(resp.access to resp.refresh)
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/exchange") {
                setBody(data)
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(resp, response.body())
            }
        }
    }

    @Test
    fun `test POST exchange - error`() = runBlocking {
        // GIVEN
        declareSuspendMock<AuthUseCase> {
            whenever(exchange(any())).thenThrow(IllegalStateException())
        }

        // WHEN
        withRouters({ authRouters() }) {
            val call = handleRequest(HttpMethod.Post, "/exchange") {
                setJsonBody(telegramDataDto(tgBotId))
            }

            // THEN
            with(call) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }
}
