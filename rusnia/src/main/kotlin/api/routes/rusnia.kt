package api.routes

import api.dto.LossesDto
import domain.interactor.GetLossesUseCase
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.rusniaRoutes(useCase: GetLossesUseCase = Provider.getLossesUseCase) {
    get("/rysni-pryzda") {
        call.respond(LossesDto(useCase()))
    }
}
