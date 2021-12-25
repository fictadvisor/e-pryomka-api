package com.fictadvisor.pryomka.api.mappers

import com.fictadvisor.pryomka.api.dto.ApplicationDto
import com.fictadvisor.pryomka.api.dto.UserDto
import com.fictadvisor.pryomka.api.dto.UserListDto
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.User

fun Application.toDto() = ApplicationDto(
    id = id.value.toString(),
    status = status,
    documents = documents
)

fun List<User>.toUserListDto() = UserListDto(
    users = map { user ->
        UserDto(
            id = user.id.value.toString(),
            name = user.name,
        )
    }
)
