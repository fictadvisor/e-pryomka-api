package com.fictadvisor.pryomka.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateOperatorDto(
    val name: String,
)
