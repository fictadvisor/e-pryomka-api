package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import java.io.InputStream

/** Encapsulates business logic of obtaining documents of an application. */
interface GetDocumentsUseCase {
    /** Searches for both metadata and content of a document that belongs to a given application and has given type.
     * @return pair of [DocumentMetadata] and [InputStream] of a document content or null if no document found. */
    suspend fun get(applicationId: ApplicationIdentifier, type: DocumentType): Pair<DocumentMetadata, InputStream>?
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
}
