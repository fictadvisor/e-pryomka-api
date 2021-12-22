package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.utils.pathFor
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import java.io.InputStream

fun Route.myApplicationRouters() {
    get("/applications/my") {
        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val application = Provider.getApplicationUseCase(userId) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
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

        val application = async {
            Provider.getApplicationUseCase(userId) ?: Provider.createApplicationUseCase(userId)
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

        val document = application.await().let { application ->
            val type = fileType ?: run {
                call.respond(HttpStatusCode.BadRequest, "File type is not provided")
                return@post
            }

            DocumentMetadata(
                applicationId = application.id,
                path = application.pathFor(
                    fileName ?: run {
                        call.respond(HttpStatusCode.BadRequest, "File name not provided")
                        return@post
                    },
                    type
                ),
                type = type,
                key = ""
            )
        }

        val content = stream ?: run {
            call.respond(HttpStatusCode.BadRequest, "Failed to read file content")
            return@post
        }

        Provider.submitDocumentUseCase(document, content)
        call.respond(HttpStatusCode.OK)
    }
}
