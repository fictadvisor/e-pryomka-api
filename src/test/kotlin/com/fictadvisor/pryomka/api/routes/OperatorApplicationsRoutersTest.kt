package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.*
import com.fictadvisor.pryomka.api.dto.ApplicationListDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.interactors.ApplicationUseCase
import com.fictadvisor.pryomka.domain.interactors.GetDocumentsUseCase
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.whenever
import com.fictadvisor.pryomka.withRouters
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class OperatorApplicationsRoutersTest {
    private val applicationUseCase: ApplicationUseCase = mock()
    private val getDocumentsUseCase: GetDocumentsUseCase = mock()

    @Test
    fun `test GET applications`() = runBlocking {
        // GIVEN
        val applications = listOf(
            application(),
            application(),
            application(),
        )

        val dto = ApplicationListDto(applications.map { it.toDto() })

        whenever(applicationUseCase.getAll()).thenReturn(applications)

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(dto, response.body())
            }
        }
    }

    @Test
    fun `test GET applications - empty list`() = runBlocking {
        // GIVEN
        val dto = ApplicationListDto(listOf())
        whenever(applicationUseCase.getAll()).thenReturn(listOf())

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(dto, response.body())
            }
        }
    }

    @Test
    fun `test GET application by id`() = runBlocking {
        // GIVEN
        val application = application()
        whenever(applicationUseCase.getById(application.id)).thenReturn(application)

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${application.id.value}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(application.toDto(), response.body())
            }
        }
    }

    @Test
    fun `test GET application by id - not found`() = runBlocking {
        // GIVEN
        val application = application()
        whenever(applicationUseCase.getById(application.id)).thenReturn(null)

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${application.id.value}").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `test GET application by id - invalid id`() {
        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/lelouch").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Invalid application id", response.content)
            }
        }
    }

    @Test
    fun `test GET application documents - invalid id`() {
        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/lelouch/documents").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Invalid application id", response.content)
            }
        }
    }

    @Test
    fun `test GET application documents - no type`() {
        // GIVEN
        val id = generateApplicationId().value

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${id}/documents").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Document type should be provided", response.content)
            }
        }
    }

    @Test
    fun `test GET application documents - invalid type`() {
        // GIVEN
        val id = generateApplicationId().value

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${id}/documents?type=lelouch").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Document type should be provided", response.content)
            }
        }
    }

    @Test
    fun `test GET application documents - document not found`() = runBlocking {
        // GIVEN
        val type = DocumentType.Contract
        val id = generateApplicationId()
        whenever(getDocumentsUseCase.get(id, type)).thenReturn(null)

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${id.value}/documents?type=${type.name}").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `test GET application documents`() = runBlocking {
        // GIVEN
        val type = DocumentType.Contract
        val id = generateApplicationId()
        val metadata = DocumentMetadata(
            applicationId = id,
            path = Path("/documents/user/contract.doc"),
            type = type,
            key = "1234"
        )
        val content = """
            It’s not that I want an ideal country or great justice
            or anything complicated like that. I just want to see people smiling.
        """.trimIndent()

        val fileNameHeader = ContentDisposition.Attachment.withParameter(
            ContentDisposition.Parameters.FileName,
            "contract.doc",
        ).toString()


        whenever(getDocumentsUseCase.get(id, type)).thenReturn(metadata to content.byteInputStream())

        // WHEN + THEN
        withRouters({ operatorApplicationsRouters(applicationUseCase, getDocumentsUseCase) }) {
            handleRequest(HttpMethod.Get, "/applications/${id.value}/documents?type=${type.name}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(fileNameHeader, response.headers[HttpHeaders.ContentDisposition])
                assertEquals(content, response.content)
            }
        }
    }
}
