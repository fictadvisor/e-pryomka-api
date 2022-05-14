package com.fictadvisor.pryomka.api

import api.routes.rusniaRoutes
import com.fictadvisor.pryomka.api.dto.CreateOperatorDto
import com.fictadvisor.pryomka.api.routes.*
import com.fictadvisor.pryomka.api.routes.faculty.learningFormatsAdminRoutes
import com.fictadvisor.pryomka.api.routes.faculty.learningFormatsRoutes
import com.fictadvisor.pryomka.api.routes.faculty.specialityAdminRoutes
import com.fictadvisor.pryomka.api.routes.faculty.specialityRoutes
import com.fictadvisor.pryomka.domain.interactors.RegisterStaffUseCase
import com.fictadvisor.pryomka.domain.models.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val registerStaffUseCase: RegisterStaffUseCase by inject()

    routing {
        authenticate(AUTH_GENERAL) {
            generalApplicationsRouters()
            specialityRoutes()
            learningFormatsRoutes()
            meRoute()
        }

        authenticate(AUTH_ADMIN) {
            operatorsRoutes()
            specialityAdminRoutes()
            learningFormatsAdminRoutes()
        }

        authenticate(AUTH_OPERATOR) {
            operatorApplicationsRouters()
        }

        authenticate(AUTH_ENTRANT) {
            myApplicationsRouters()
        }

        authRouters()

        get("/") {
            call.respondText("\uD83E\uDDD1\u200D\uD83D\uDCBBWelcome to FICT!\uD83D\uDE80")
        }

        post<CreateOperatorDto>("/register_admin") { (login, password) ->
            try {
                registerStaffUseCase.register(login, password, User.Staff.Role.Admin)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        rusniaRoutes()
    }
}

fun Application.configureServer() {
    install(ContentNegotiation) { json() }
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        anyHost() // TODO: Reconsider
    }
}
