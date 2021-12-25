package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val id: String,
    val status: Application.Status,
    val documents: Set<DocumentType>,
)
