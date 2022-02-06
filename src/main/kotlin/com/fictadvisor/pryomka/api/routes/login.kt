package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.LogInRequestDto
import com.fictadvisor.pryomka.api.dto.LogInResponseDto
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.Exception

fun Route.loginRouters() {
    post<LogInRequestDto>("/login") { (login, password) ->
        try {
            val token = Provider.authUseCase.logIn(login, password)
            call.respond(LogInResponseDto(token))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
