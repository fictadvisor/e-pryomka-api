package com.fictadvisor.pryomka.domain.models

/**
 * Metadata of a stored document.
 * @param applicationId id of application document belongs to
 * @param path path in the filesystem where the document is stored
 * @param type type of the document
 * @param key encryption key to access document content. */
data class DocumentMetadata(
    val applicationId: ApplicationIdentifier,
    val path: Path,
    val type: DocumentType,
    val key: DocumentKey,
)
