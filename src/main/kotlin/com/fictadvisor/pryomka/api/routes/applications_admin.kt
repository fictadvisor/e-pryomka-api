package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.CreateOperatorDto
import com.fictadvisor.pryomka.api.mappers.toUserListDto
import com.fictadvisor.pryomka.domain.interactors.OperatorManagementUseCases
import com.fictadvisor.pryomka.domain.models.toUserIdentifierOrNull
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.adminApplicationsRouters(
    useCase: OperatorManagementUseCases = Provider.operatorManagementUseCases,
) {
    get("/operators") {
        val users = useCase.getAll()
        call.respond(users.toUserListDto())
    }

    post<CreateOperatorDto>("/operators") { operator ->
        if (operator.name.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            useCase.add(operator.name)
            call.respond(HttpStatusCode.OK)
        } catch (e: IllegalStateException) {
            call.respond(HttpStatusCode.Conflict, e.message.orEmpty())
        }
    }

    delete("/operators/{id}") {
        val id = call.parameters["id"]
            ?.toUserIdentifierOrNull()
            ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

        try {
            useCase.delete(id)
            call.respond(HttpStatusCode.OK)
        } catch (e: IllegalStateException) {
            call.respond(HttpStatusCode.NotFound, e.message.orEmpty())
        }
    }
}
