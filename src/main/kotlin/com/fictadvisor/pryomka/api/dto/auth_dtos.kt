package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class LogInRequestDto(
    val login: String,
    val password: String,
)

@Serializable
data class LogInResponseDto(
    val access: String,
    val refresh: String,
)

@Serializable
data class RefreshRequest(
    val refresh: String,
)

@Serializable
data class WhoAmIDto(
    val id: String,
    val name: String,
    val role: User.Staff.Role? = null,
    val photoUrl: String? = null,
)
