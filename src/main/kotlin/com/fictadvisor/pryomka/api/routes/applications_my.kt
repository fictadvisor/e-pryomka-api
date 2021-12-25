package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.ApplicationRequestDto
import com.fictadvisor.pryomka.api.mappers.toDomain
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.interactors.ApplicationUseCase
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.utils.pathFor
import com.fictadvisor.pryomka.utils.toUUIDOrNull
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import java.io.InputStream

fun Route.myApplicationsRouters() {
    get("/applications/my") {
        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        val applications = Provider.applicationUseCase.getByUserId(userId)
        call.respond(applications.map(Application::toDto))
    }

    post<ApplicationRequestDto>("/applications/my") { applicationRequest ->
        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        val application = applicationRequest.toDomain(userId)
        val applicationUseCase = Provider.applicationUseCase

        try {
            applicationUseCase.create(application, userId)
            call.respond(HttpStatusCode.OK, application.toDto())
        } catch (e: ApplicationUseCase.Duplicated) {
            call.respond(HttpStatusCode.Conflict, e.message.orEmpty())
        }
    }

    post("/applications/{id}/documents") {
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@post
        }

        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        var fileType: DocumentType? = null
        var stream: InputStream? = null
        var fileName: String? = null

        val applicationDef = async {
            Provider.applicationUseCase.get(ApplicationIdentifier(id), userId)
        }

        try {
            call.receiveMultipart().forEachPart { part -> when (part) {
                is PartData.FormItem -> {
                    if (part.name == "FileType") fileType = DocumentType.valueOf(part.value)
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName
                    stream = part.streamProvider()
                }
                else -> {}
            }}
        } catch (e: IllegalStateException) { /* nothing */ }

        val document = applicationDef.await()?.let { application ->
            val type = fileType ?: run {
                call.respond(HttpStatusCode.BadRequest, "File type is not provided")
                return@post
            }

            DocumentMetadata(
                applicationId = application.id,
                path = application.pathFor(
                    fileName ?: run {
                        call.respond(HttpStatusCode.BadRequest, "File name is not provided")
                        return@post
                    },
                    type
                ),
                type = type,
                key = "",
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@post
        }

        val content = stream ?: run {
            call.respond(HttpStatusCode.BadRequest, "Failed to read file content")
            return@post
        }

        Provider.submitDocumentUseCase(document, content)
        call.respond(HttpStatusCode.OK)
    }
}
