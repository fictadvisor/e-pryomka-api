package com.fictadvisor.pryomka.api.dto.faculty

import kotlinx.serialization.Serializable

@Serializable
data class SpecialityDto(
    val code: Int,
    val name: String,
)
