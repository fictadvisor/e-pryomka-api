package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.api.dto.faculty.LearningFormatDto
import com.fictadvisor.pryomka.api.dto.faculty.SpecialityDto
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseDto(
    val id: String,
    val documents: Set<DocumentType>,
    val speciality: SpecialityDto,
    val funding: Application.Funding,
    @SerialName("learning_format")
    val learningFormat: LearningFormatDto,
    @SerialName("created_at")
    val createdAt: Instant,
    val status: Application.Status,
    @SerialName("status_message")
    val statusMessage: String? = null,
)

@Serializable
data class ApplicationListDto(
    val applications: List<ApplicationResponseDto>
)

@Serializable
data class ApplicationRequestDto(
    val speciality: SpecialityDto,
    val funding: Application.Funding,
    @SerialName("learning_format")
    val learningFormat: LearningFormatDto,
)
