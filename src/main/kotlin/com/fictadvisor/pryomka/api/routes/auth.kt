package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.Provider
import com.fictadvisor.pryomka.api.dto.LogInRequestDto
import com.fictadvisor.pryomka.api.dto.LogInResponseDto
import com.fictadvisor.pryomka.api.dto.RefreshRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.Exception

fun Route.authRouters() {
    post<LogInRequestDto>("/login") { (login, password) ->
        try {
            val (access, refresh) = Provider.authUseCase.logIn(login, password)
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post<RefreshRequest>("/refresh") { (token) ->
        try {
            val (access, refresh) = Provider.authUseCase.refresh(token)
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post<Map<String, String>>("/exchange") { telegramData ->
        try {
            val (access, refresh) = Provider.authUseCase.exchange(telegramData)
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
