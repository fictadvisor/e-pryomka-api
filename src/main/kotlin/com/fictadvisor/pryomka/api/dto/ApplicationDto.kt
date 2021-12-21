package com.fictadvisor.pryomka.api.dto

import com.fictadvisor.pryomka.domain.models.DocumentType
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDto(
    val documents: Map<DocumentType, DocumentDto>
)
