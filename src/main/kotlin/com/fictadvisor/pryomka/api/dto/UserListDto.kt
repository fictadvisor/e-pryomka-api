package com.fictadvisor.pryomka.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserListDto(
    val users: List<UserDto>
)
