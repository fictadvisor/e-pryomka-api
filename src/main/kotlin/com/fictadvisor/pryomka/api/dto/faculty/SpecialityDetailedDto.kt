package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecialityDetailedDto(
    val code: Int,
    val name: String,
    @SerialName("learning_formats")
    val learningFormats: List<LearningFormatDto>
)
