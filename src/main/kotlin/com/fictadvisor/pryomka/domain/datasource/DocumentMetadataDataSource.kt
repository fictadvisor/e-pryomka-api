package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType

/** Provides methods to manipulate documents metadata in the database. */
interface DocumentMetadataDataSource {
    /** Stores given document metadata. */
    suspend fun add(document: DocumentMetadata)

    /** Replaces metadata using application id and document type. */
    suspend fun replace(document: DocumentMetadata)

    /** Searches for the particular document of given application. Every application
     * can have only one document of each type.
     * @param applicationId id of the application that document belongs to
     * @param type type of the document to search.
     * @return metadata of the document or null if nothing was found */
    suspend fun find(applicationId: ApplicationIdentifier, type: DocumentType): DocumentMetadata?
}
