package com.fictadvisor.pryomka.api.routes.faculty

import com.fictadvisor.pryomka.api.dto.faculty.AllDetailedSpecialitiesDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityLearningFormatsDto
import com.fictadvisor.pryomka.api.mappers.toDetailedDto
import com.fictadvisor.pryomka.api.mappers.toDomain
import com.fictadvisor.pryomka.domain.interactors.faculty.SpecialitiesUseCases
import com.fictadvisor.pryomka.domain.models.toLearningFormatIdentifier
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.specialityRoutes() {
    val specialitiesUseCases: SpecialitiesUseCases by inject()

    get("/specialities") {
        try {
            val specialities = specialitiesUseCases.allSpecialitiesWithLearningFormats()
            val dto = specialities
                .map { (spec, formats) -> spec.toDetailedDto(formats) }
                .let { AllDetailedSpecialitiesDto(it) }

            call.respond(dto)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

fun Route.specialityAdminRoutes() {
    val specialitiesUseCases: SpecialitiesUseCases by inject()

    post<SpecialityDto>("/specialities") { dto ->
        try {
            specialitiesUseCases.createSpeciality(dto.toDomain())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    put<SpecialityDto>("/specialities/{code}") { dto ->
        val code = call.parameters["code"]
            ?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest)

        try {
            specialitiesUseCases.editSpeciality(code, dto.toDomain())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    delete("/specialities/{code}") {
        val code = call.parameters["code"]
            ?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest)

        try {
            specialitiesUseCases.deleteSpeciality(code)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    put<SpecialityLearningFormatsDto>("/specialities/{code}/learning-formats") { dto ->
        val code = call.parameters["code"]
            ?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest)

        try {
            val learningFormatIds = dto.learningFormats.map { it.toLearningFormatIdentifier() }
            specialitiesUseCases.setLearningFormats(code, learningFormatIds)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
