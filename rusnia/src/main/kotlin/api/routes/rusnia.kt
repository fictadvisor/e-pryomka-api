package api.routes

import api.dto.LossesDto
import domain.interactor.GetLossesUseCase
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.rusniaRoutes(useCase: GetLossesUseCase = Provider.getLossesUseCase) {
    get("/rysni-pryzda") {
        val locale = call.request.queryParameters["l"]?.let { Locale(it) }
        try {
            call.respond(LossesDto(useCase(locale)))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "")
        }
    }

    delete ("/rysni-pryzda") {
        val password = call.receiveText()

        if (password != Provider.password) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            useCase.invalidateCache()
            call.respond(HttpStatusCode.OK)
        }
    }
}
