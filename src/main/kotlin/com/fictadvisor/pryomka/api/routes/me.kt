package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.mappers.toWhoAmIDto
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.meRoute() {
    get("/me") {
        val token = call.request
            .header("Authorization")
            ?.substringAfter("Bearer")
            ?.trim()

        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        try {
            val user = Provider.authUseCase.getMe(token) ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            call.respond(user.toWhoAmIDto())
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}