package com.fictadvisor.pryomka.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LogInRequestDto(
    val login: String,
    val password: String,
)

@Serializable
data class LogInResponseDto(
    val token: String
)
