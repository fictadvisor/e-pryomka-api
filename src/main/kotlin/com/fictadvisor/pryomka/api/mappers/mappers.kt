package com.fictadvisor.pryomka.api.mappers

import com.fictadvisor.pryomka.api.dto.*
import com.fictadvisor.pryomka.api.dto.faculty.LearningFormatDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityDetailedDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityDto
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.datetime.Clock

fun Application.toDto() = ApplicationResponseDto(
    id = id.value.toString(),
    status = status,
    documents = documents,
    speciality = speciality.toDto(),
    funding = funding,
    createdAt = createdAt,
    learningFormat = learningFormat.toDto(),
    statusMessage = statusMessage,
)

fun ApplicationRequestDto.toDomain(userId: UserIdentifier) = Application(
    id = generateApplicationId(),
    userId = userId,
    documents = setOf(),
    speciality = speciality.toDomain(),
    funding = funding,
    learningFormat = learningFormat.toDomain(),
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

fun TelegramDataDto.toTelegramData() = TelegramData(
    authDate,
    firstName,
    id,
    lastName,
    photoUrl,
    userName,
    hash,
)

fun SpecialityDto.toDomain() = Speciality(code, name)

fun LearningFormatDto.toDomain() = LearningFormat(
    id.toLearningFormatIdentifier(),
    name,
)

fun Speciality.toDto() = SpecialityDto(
    code = code,
    name = name,
)

fun Speciality.toDetailedDto(learningFormats: List<LearningFormat>) = SpecialityDetailedDto(
    code = code,
    name = name,
    learningFormats = learningFormats.map { it.toDto() }
)

fun LearningFormat.toDto() = LearningFormatDto(
    id = id.value.toString(),
    name = name,
)
