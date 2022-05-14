package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.Serializable

@Serializable
data class LearningFormatDto(
    val id: String,
    val name: String,
)
