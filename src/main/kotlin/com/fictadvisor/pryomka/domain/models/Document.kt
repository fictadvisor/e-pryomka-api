package com.fictadvisor.pryomka.domain.models

data class DocumentMetadata(
    val applicationId: ApplicationIdentifier,
    val path: Path,
    val type: DocumentType,
    val key: DocumentKey,
)
