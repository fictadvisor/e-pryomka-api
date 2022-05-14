package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.api.dto.ChangeApplicationStatusDto
import com.fictadvisor.pryomka.domain.interactors.ChangeApplicationStatusUseCase
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.generalApplicationsRouters() {
    val useCase: ChangeApplicationStatusUseCase by inject()

    put<ChangeApplicationStatusDto>("/applications/{id}") { changeStatusDto ->
        val id = call.parameters["id"]
            ?.toApplicationIdentifierOrNull()
            ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid application id")
                return@put
            }

        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@put
        }

        try {
            useCase.changeStatus(id, userId, changeStatusDto.status, changeStatusDto.statusMessage)
            call.respond(HttpStatusCode.OK)
        } catch (e: Unauthorized) {
            call.respond(HttpStatusCode.Unauthorized)
        } catch (e: NotFound) {
            call.respond(HttpStatusCode.NotFound, e.message.orEmpty())
        } catch (e: PermissionDenied) {
            call.respond(HttpStatusCode.Forbidden, e.message.orEmpty())
        }
    }
}
