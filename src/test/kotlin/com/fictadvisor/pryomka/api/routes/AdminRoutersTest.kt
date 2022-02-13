package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.api.dto.CreateOperatorDto
import com.fictadvisor.pryomka.api.mappers.toUserListDto
import com.fictadvisor.pryomka.body
import com.fictadvisor.pryomka.domain.interactors.OperatorManagementUseCases
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId
import com.fictadvisor.pryomka.setJsonBody
import com.fictadvisor.pryomka.withRouters
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.Mockito.times
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminRoutersTest {
    private val useCase = Mockito.mock(OperatorManagementUseCases::class.java)

    @Test
    fun `test GET operators`() = runBlocking {
        // GIVEN
        val users = listOf(
            User(generateUserId(), "User 1", User.Role.Operator),
            User(generateUserId(), "User 2", User.Role.Operator),
            User(generateUserId(), "User 3", User.Role.Operator),
        )

        Mockito.`when`(useCase.getAll()).thenReturn(users)

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
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
        val users = listOf<User>()

        Mockito.`when`(useCase.getAll()).thenReturn(users)

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
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
        Mockito.`when`(useCase.add(any())).thenReturn(Unit)

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("Lelouch Lamperouge"))
            }

            with(call) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content.isNullOrEmpty())
                runBlocking {
                    Mockito.verify(useCase, times(1)).add(any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators - empty name`() = runBlocking {
        // GIVEN
        Mockito.`when`(useCase.add(any())).thenReturn(Unit)

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto(""))
            }

            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                runBlocking {
                    Mockito.verify(useCase, times(0)).add(any())
                }
            }
        }

        return@runBlocking
    }

    @Test
    fun `test POST operators - duplicated user`() = runBlocking {
        // GIVEN
        Mockito.`when`(useCase.add(any())).thenThrow(IllegalStateException("User already exists"))

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
            val call = handleRequest(HttpMethod.Post, "/operators") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setJsonBody(CreateOperatorDto("Lelouch vi Britannia"))
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
        Mockito.`when`(useCase.delete(operatorId)).thenReturn(Unit)

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
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
        Mockito.`when`(useCase.delete(operatorId))
            .thenThrow(IllegalStateException("User does not exist"))

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
            val call = handleRequest(HttpMethod.Delete, "/operators/${operatorId.value}")
            with(call) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }

        return@runBlocking
    }

    @Test
    fun `test DELETE operators - invalid id`() = runBlocking {
        // GIVEN

        // WHEN + THEN
        withRouters({ operatorsRoutes(useCase) }) {
            val call = handleRequest(HttpMethod.Delete, "/operators/abcd")
            with(call) {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }

        return@runBlocking
    }
}
