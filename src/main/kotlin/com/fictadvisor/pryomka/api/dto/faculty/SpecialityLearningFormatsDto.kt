package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecialityLearningFormatsDto(
    @SerialName("learning_formats")
    val learningFormats: List<String>,
)
