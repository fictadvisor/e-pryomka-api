package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.mappers.toWhoAmIDto
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.meRoute(authUseCase: AuthUseCase = Provider.authUseCase) {
    get("/me") {
        val token = call.request
            .header("Authorization")
            ?.substringAfter("Bearer")
            ?.trim()

        if (token.isNullOrEmpty()) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        try {
            val user = authUseCase.getMe(token) ?: run {
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