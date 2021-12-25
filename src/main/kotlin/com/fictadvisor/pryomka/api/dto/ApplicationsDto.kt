package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseDto(
    val id: String,
    val documents: List<DocumentType>,
    val speciality: Application.Speciality,
    val funding: Application.Funding,
    val learningFormat: Application.LearningFormat,
    val createdAt: Instant,
    val status: Application.Status,
    val statusMsg: String? = null,
)

@Serializable
data class ApplicationRequestDto(
    val speciality: Application.Speciality,
    val funding: Application.Funding,
    val learningFormat: Application.LearningFormat,
)
