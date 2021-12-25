package com.fictadvisor.pryomka.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
)

@Serializable
data class UserListDto(
    val users: List<UserDto>
)

