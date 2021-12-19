package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.domain.models.Document
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.serialization.*
import java.io.InputStream
import java.util.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/applications/all") {}

        get("/applications/my") {}
        post("/applications/my") {
            val submitDocumentUseCase = Provider.submitDocumentUseCase
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

            val path = Path("uploads/" + (fileName ?: error("Filename is not provided")))
            val doc = Document(path)

            submitDocumentUseCase(
                userIdentifier = UserIdentifier(UUID(0, 0)),
                document = doc,
                type = fileType ?: error("File type is not provided"),
                content = stream ?: error("Failed to read file content")
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/") {
            println("HERE")
            call.respond(mapOf("Test" to "Kek"))
        }
    }
}
