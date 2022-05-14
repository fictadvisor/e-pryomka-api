package com.fictadvisor.pryomka.api.routes.faculty

import com.fictadvisor.pryomka.api.dto.faculty.AllLearningFormatsDto
import com.fictadvisor.pryomka.api.dto.faculty.CreateLearningFormatDto
import com.fictadvisor.pryomka.api.dto.faculty.LearningFormatDto
import com.fictadvisor.pryomka.api.mappers.toDto
import com.fictadvisor.pryomka.domain.interactors.faculty.LearningFormatsUseCases
import com.fictadvisor.pryomka.domain.models.toLearningFormatIdentifier
import com.fictadvisor.pryomka.domain.models.toLearningFormatIdentifierOrNull
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.learningFormatsRoutes() {
    val learningFormatsUseCases: LearningFormatsUseCases by inject()

    get("/learning-formats") {
        try {
            val formats = learningFormatsUseCases.getAllFormats().map { it.toDto() }
            call.respond(AllLearningFormatsDto(formats))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

fun Route.learningFormatsAdminRoutes() {
    val learningFormatsUseCases: LearningFormatsUseCases by inject()

    post<CreateLearningFormatDto>("/learning-formats") { dto ->
        try {
            val format = learningFormatsUseCases.createFormat(dto.name)
            call.respond(format.toDto())
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    put<LearningFormatDto>("/learning-formats") { dto ->
        try {
            learningFormatsUseCases.editFormat(dto.id.toLearningFormatIdentifier(), dto.name)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    delete("/learning-formats/{id}") {
        val id = call.parameters["id"]
            ?.toLearningFormatIdentifierOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest)

        try {
            learningFormatsUseCases.deleteFormat(id)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
