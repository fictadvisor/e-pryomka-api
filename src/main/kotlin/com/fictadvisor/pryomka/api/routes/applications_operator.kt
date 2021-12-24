package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.ChangeApplicationStatusDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.utils.toUUIDOrNull
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.nio.file.Files
import java.util.*


fun Route.operatorApplicationsRouters() {
    get("/applications") {
        val applications = Provider.applicationUseCase.getAll()
        call.respond(applications.map(Application::toDto))
    }

    get("/applications/{id}") {
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@get
        }

        val application = Provider.applicationUseCase.getById(ApplicationIdentifier(id)) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(application.toDto())
    }

    // todo think about sending all documents if type is not provided
    get("/applications/{id}/documents") {
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@get
        }

        val type = call.request.queryParameters["type"]?.let {
            DocumentType.fromString(it)
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Document type should be provided")
            return@get
        }

        val (metadata, content) = Provider.getDocumentsUseCase.get(ApplicationIdentifier(id), type) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val fileName = metadata.path.value.substringAfterLast("/")
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                fileName,
            ).toString(),
        )

        content.use {
            call.respondOutputStream { it.copyTo(this) }
        }
    }

    put<ChangeApplicationStatusDto>("/applications/{id}") { changeStatusDto ->
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@put
        }

        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@put
        }

        Provider.applicationUseCase.changeStatus()
    }
}
