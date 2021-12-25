package com.fictadvisor.pryomka.api.mappers

import com.fictadvisor.pryomka.api.dto.ApplicationDto
import com.fictadvisor.pryomka.domain.models.Application

fun Application.toDto() = ApplicationDto(
    id = id.value.toString(),
    status = status,
    documents = documents.toList()
)
