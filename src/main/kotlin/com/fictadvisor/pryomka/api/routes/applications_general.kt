package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.ChangeApplicationStatusDto
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.NotFound
import com.fictadvisor.pryomka.domain.models.PermissionDenied
import com.fictadvisor.pryomka.domain.models.Unauthorized
import com.fictadvisor.pryomka.utils.toUUIDOrNull
import com.fictadvisor.pryomka.utils.userId
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.generalApplicationsRouters() {
    put<ChangeApplicationStatusDto>("/applications/{id}") { changeStatusDto ->
        val id = call.parameters["id"]?.toUUIDOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid application id")
            return@put
        }

        val userId = call.userId ?: run {
            call.respond(HttpStatusCode.Unauthorized)
            return@put
        }

        try {
            Provider.changeApplicationStatusUseCase.changeStatus(
                ApplicationIdentifier(id),
                userId,
                changeStatusDto.status,
                changeStatusDto.statusMessage,
            )

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
