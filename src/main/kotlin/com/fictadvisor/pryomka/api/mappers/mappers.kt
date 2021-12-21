package com.fictadvisor.pryomka.api.mappers

import com.fictadvisor.pryomka.api.dto.ApplicationDto
import com.fictadvisor.pryomka.api.dto.DocumentDto
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.Document

fun Document.toDto() = DocumentDto(
    id = path.value
)

fun Application.toDto() = ApplicationDto(
    documents = documents.mapValues { (_, v) -> v.toDto() }
)
