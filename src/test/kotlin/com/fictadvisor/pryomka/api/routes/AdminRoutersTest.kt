package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.dto.CreateOperatorDto
import com.fictadvisor.pryomka.api.mappers.toUserListDto
import com.fictadvisor.pryomka.domain.interactors.OperatorManagementUseCases
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminRoutersTest : KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    val koinTestRule = KoinTestRule.create {}

    @Test
    fun `test GET operators`() = runBlocking {
        // GIVEN
        val users = listOf(
            User.Staff(generateUserId(), "User 1", User.Staff.Role.Operator),
            User.Staff(generateUserId(), "User 2", User.Staff.Role.Operator),
            User.Staff(generateUserId(), "User 3", User.Staff.Role.Operator),
        )

        declareSuspendMock<OperatorManagementUseCases> {
            whenever(getAll()).thenReturn(users)
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            handleRequest(HttpMethod.Get, "/operators").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(users.toUserListDto(), response.body())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test GET operators - no operators`() = runBlocking {
        // GIVEN
        val users = listOf<User.Staff>()

        declareSuspendMock<OperatorManagementUseCases> {
            whenever(getAll()).thenReturn(users)
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            handleRequest(HttpMethod.Get, "/operators").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(users.toUserListDto(), response.body())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators`() = runBlocking {
        // GIVEN
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(add(any(), any())).thenReturn(
                User.Staff(
                    id = generateUserId(),
                    name = "Lelouch Lamperouge",
                    role = User.Staff.Role.Operator
                )
            )
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("lelouch", "lamperouge"))
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content.isNullOrEmpty())
                runBlocking {
                    Mockito.verify(get<OperatorManagementUseCases>(), times(1)).add(any(), any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators - empty name`() = runBlocking {
        // GIVEN
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(add(any(), any())).thenReturn(
                User.Staff(
                    id = generateUserId(),
                    name = "Lelouch Lamperouge",
                    role = User.Staff.Role.Operator
                )
            )
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("", "lamperouge"))
            }

            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                runBlocking {
                    Mockito.verify(get<OperatorManagementUseCases>(), times(0)).add(any(), any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators - empty password`() = runBlocking {
        // GIVEN
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(add(any(), any())).thenReturn(
                User.Staff(
                    id = generateUserId(),
                    name = "Lelouch Lamperouge",
                    role = User.Staff.Role.Operator
                )
            )
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("lamperouge", ""))
            }

            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                runBlocking {
                    Mockito.verify(get<OperatorManagementUseCases>(), times(0)).add(any(), any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators - duplicated user`() = runBlocking {
        // GIVEN
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(add(any(), any())).thenThrow(IllegalStateException("User already exists"))
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("Lelouch", "vi_Britannia"))
            }

            with(call) {
                assertEquals(HttpStatusCode.Conflict, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test DELETE operators`() = runBlocking {
        // GIVEN
        val operatorId = generateUserId()
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(delete(operatorId)).thenReturn(Unit)
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Delete, "/operators/${operatorId.value}")
            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test DELETE operators - user does not exist`() = runBlocking {
        // GIVEN
        val operatorId = generateUserId()
        declareSuspendMock<OperatorManagementUseCases> {
            whenever(delete(operatorId)).thenThrow(IllegalStateException("User does not exist"))
        }

        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Delete, "/operators/${operatorId.value}")
            with(call) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test DELETE operators - invalid id`() = runBlocking {
        // WHEN + THEN
        withRouters({ operatorsRoutes() }) {
            val call = handleRequest(HttpMethod.Delete, "/operators/abcd")
            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        return@runBlocking
    }
}
