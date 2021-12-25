package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.Application
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeApplicationStatusDto(
    val status: Application.Status,
    @SerialName("status_msg")
    val statusMsg: String? = null,
)
