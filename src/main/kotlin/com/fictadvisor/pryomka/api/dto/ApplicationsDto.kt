package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseDto(
    val id: String,
    val documents: Set<DocumentType>,
    val speciality: Application.Speciality,
    val funding: Application.Funding,
    @SerialName("learning_format")
    val learningFormat: Application.LearningFormat,
    @SerialName("created_at")
    val createdAt: Instant,
    val status: Application.Status,
    @SerialName("status_msg")
    val statusMsg: String? = null,
)

@Serializable
data class ApplicationRequestDto(
    val speciality: Application.Speciality,
    val funding: Application.Funding,
    @SerialName("learning_format")
    val learningFormat: Application.LearningFormat,
)
