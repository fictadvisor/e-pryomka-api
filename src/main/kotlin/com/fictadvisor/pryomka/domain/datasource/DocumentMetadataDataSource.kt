package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.*

interface DocumentMetadataDataSource {
    suspend fun add(document: DocumentMetadata)
    suspend fun replace(document: DocumentMetadata)
    suspend fun find(applicationId: ApplicationIdentifier, type: DocumentType): DocumentMetadata?
}
