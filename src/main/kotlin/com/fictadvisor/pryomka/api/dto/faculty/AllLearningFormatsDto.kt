package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.Serializable

@Serializable
data class AllLearningFormatsDto(val formats: List<LearningFormatDto>)
