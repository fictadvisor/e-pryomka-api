package com.fictadvisor.pryomka.domain.models.faculty

import com.fictadvisor.pryomka.domain.models.LearningFormatIdentifier

data class LearningFormat(
    val id: LearningFormatIdentifier,
    val name: String,
)
