package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.Application
import kotlinx.serialization.Serializable

@Serializable
data class ChangeApplicationStatusDto(
    val status: Application.Status,
    val statusMsg: String? = null,
)
