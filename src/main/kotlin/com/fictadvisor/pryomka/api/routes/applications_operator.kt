package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.api.dto.ApplicationListDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.interactors.ApplicationUseCase
import com.fictadvisor.pryomka.domain.interactors.GetDocumentsUseCase
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.toApplicationIdentifierOrNull
import com.fictadvisor.pryomka.utils.toUUIDOrNull
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject


fun Route.operatorApplicationsRouters() {
    val applicationUseCase: ApplicationUseCase by inject()
    val getDocumentsUseCase: GetDocumentsUseCase by inject()

    get("/applications") {
        val applications = applicationUseCase.getAll()
        call.respond(ApplicationListDto(applications.map(Application::toDto)))
    }

    get("/applications/{id}") {
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@get
        }

        val application = applicationUseCase.getById(ApplicationIdentifier(id)) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(application.toDto())
    }

    // todo think about sending all documents if type is not provided
    get("/applications/{id}/documents") {
        val id = call.parameters["id"]
            ?.toApplicationIdentifierOrNull()
            ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid application id")
                return@get
            }

        val type = call.request.queryParameters["type"]?.let {
            DocumentType.fromString(it)
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Document type should be provided")
            return@get
        }

        val (metadata, content) = getDocumentsUseCase.get(id, type) ?: run {
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
}
