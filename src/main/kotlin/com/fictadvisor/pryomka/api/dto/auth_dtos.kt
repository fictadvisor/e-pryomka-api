package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.User
import kotlinx.serialization.SerialName
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

@Serializable
data class TelegramDataDto(
    @SerialName("auth_date")
    val authDate: Long,

    val id: Long,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("username")
    val userName: String? = null,

    @SerialName("photo_url")
    val photoUrl: String? = null,

    val hash: String
)
