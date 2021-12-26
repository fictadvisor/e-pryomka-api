package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.domain.models.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.mockito.Mockito

internal inline fun <reified T> any(): T = Mockito.any(T::class.java)

fun user(
    id: UserIdentifier = generateUserId(),
    name: String = "",
    role: User.Role = User.Role.Entrant,
) = User(id, name, role)

fun application(
    id: ApplicationIdentifier = generateApplicationId(),
    userId: UserIdentifier = generateUserId(),
    documents: Set<DocumentType> = setOf(),
    funding: Application.Funding = Application.Funding.Budget,
    speciality: Application.Speciality = Application.Speciality.SPEC_121,
    learningFormat: Application.LearningFormat = Application.LearningFormat.FullTime,
    createdAt: Instant = Clock.System.now(),
    status: Application.Status = Application.Status.Pending,
    statusMsg: String? = null,
) = Application(
    id = id,
    userId = userId,
    documents = documents,
    funding = funding,
    speciality = speciality,
    learningFormat = learningFormat,
    createdAt = createdAt,
    status = status,
    statusMsg = statusMsg,
)
