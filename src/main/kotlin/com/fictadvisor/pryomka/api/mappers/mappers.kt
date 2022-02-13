package com.fictadvisor.pryomka.api.mappers

import com.fictadvisor.pryomka.api.dto.*
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import com.fictadvisor.pryomka.domain.models.generateApplicationId
import kotlinx.datetime.Clock
import com.fictadvisor.pryomka.domain.models.User

fun Application.toDto() = ApplicationResponseDto(
    id = id.value.toString(),
    status = status,
    documents = documents,
    speciality = speciality,
    funding = funding,
    createdAt = createdAt,
    learningFormat = learningFormat,
    statusMsg = statusMsg,
)

fun ApplicationRequestDto.toDomain(userId: UserIdentifier) = Application(
    id = generateApplicationId(),
    userId = userId,
    documents = setOf(),
    speciality = speciality,
    funding = funding,
    learningFormat = learningFormat,
    createdAt = Clock.System.now(),
    status = Application.Status.Preparing,
)

fun List<User.Staff>.toUserListDto() = UserListDto(
    users = map { user ->
        UserDto(
            id = user.id.value.toString(),
            name = user.name,
        )
    }
)

fun User.toWhoAmIDto() = when (this) {
    is User.Entrant -> WhoAmIDto(
        id = id.value.toString(),
        name = "$firstName ${lastName ?: ""}".trim(),
        photoUrl = photoUrl
    )
    is User.Staff -> WhoAmIDto(
        id = id.value.toString(),
        name = name,
        role = role,
    )
}
