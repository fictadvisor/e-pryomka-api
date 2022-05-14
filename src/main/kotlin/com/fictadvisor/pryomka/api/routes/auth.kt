package com.fictadvisor.pryomka.api.routes

import com.fictadvisor.pryomka.api.dto.LogInRequestDto
import com.fictadvisor.pryomka.api.dto.LogInResponseDto
import com.fictadvisor.pryomka.api.dto.RefreshRequest
import com.fictadvisor.pryomka.api.dto.TelegramDataDto
import com.fictadvisor.pryomka.api.mappers.toTelegramData
import com.fictadvisor.pryomka.domain.interactors.AuthUseCase
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import kotlin.Exception

fun Route.authRouters() {
    val authUseCase: AuthUseCase by inject()

    post<LogInRequestDto>("/login") { (login, password) ->
        try {
            val (access, refresh) = authUseCase.logIn(login, password)
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post<RefreshRequest>("/refresh") { (token) ->
        try {
            val (access, refresh) = authUseCase.refresh(token)
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post<TelegramDataDto>("/exchange") { telegramDataDto ->
        try {
            val (access, refresh) = authUseCase.exchange(telegramDataDto.toTelegramData())
            call.respond(LogInResponseDto(access, refresh))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
