package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import java.io.InputStream

interface GetDocumentsUseCase {
    suspend fun get(applicationId: ApplicationIdentifier, type: DocumentType): Pair<DocumentMetadata, InputStream>?
    suspend fun getAll(applicationId: ApplicationIdentifier)
}

class GetDocumentsUseCaseImpl(
    private val documentsContentDS: DocumentContentDataSource,
    private val documentsMetadataDS: DocumentMetadataDataSource,
) : GetDocumentsUseCase {
    override suspend fun get(
        applicationId: ApplicationIdentifier,
        type: DocumentType,
    ) : Pair<DocumentMetadata, InputStream>? {
        val metadata = documentsMetadataDS.find(applicationId, type) ?: return null
        val content = documentsContentDS.get(metadata)

        return metadata to content
    }

    override suspend fun getAll(applicationId: ApplicationIdentifier) {
        TODO("Not yet implemented")
    }
}
