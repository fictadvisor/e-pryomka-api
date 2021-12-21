package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.models.Document
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.utils.pathFor
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.InputStream

fun Route.applicationRouters() {
    get("/applications/my") {
        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val application = Provider.getApplicationUseCase(userId)
        call.respond(application.toDto())
    }

    post("/applications/my") {
        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        var fileType: DocumentType? = null
        var stream: InputStream? = null
        var fileName: String? = null

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

        val path = pathFor(
            userId,
            fileName ?: run {
                call.respond(HttpStatusCode.BadRequest, "File name not provided")
                return@post
            }
        )
        val doc = Document(path)

        Provider.submitDocumentUseCase(
            userIdentifier = userId,
            document = doc,
            type = fileType ?: run {
                call.respond(HttpStatusCode.BadRequest, "File type is not provided")
                return@post
            },
            content = stream ?: run {
                call.respond(HttpStatusCode.BadRequest, "Failed to read file content")
                return@post
            }
        )

        call.respond(HttpStatusCode.OK)
    }
}
