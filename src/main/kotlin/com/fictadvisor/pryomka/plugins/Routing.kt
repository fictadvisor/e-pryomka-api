package com.fictadvisor.pryomka.plugins

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.serialization.*
import java.io.File

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/applications/all") {}

        get("/applications/my") {}
        post("/applications/my") {
            var fileDescription = ""
            var fileName = ""
            call.receiveMultipart().forEachPart { part -> when (part) {
                is PartData.FormItem -> {
                    fileDescription = part.value
                }
                is PartData.FileItem -> {
                    println("FILE ITEM")
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    File("uploads/$fileName").writeBytes(fileBytes)
                }
                is PartData.BinaryItem -> {}
            }}
            call.respondText("$fileDescription is uploaded to uploads/$fileName")
        }

        get("/") {
            call.respond(mapOf("Test" to "Kek"))
        }
    }
}
