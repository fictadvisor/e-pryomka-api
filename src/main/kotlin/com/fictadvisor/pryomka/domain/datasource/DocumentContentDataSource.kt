package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.DocumentKey
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import java.io.InputStream

/** Provides access to the content of the documents e.g. performing
 * encryption and decryption as well as storage in the file system. */
interface DocumentContentDataSource {
    /** Saves document with the given metadata.
     * @param document metadata of the document to be saved including its path.
     * @param data document content
     * @return encryption key to read document content */
    suspend fun save(document: DocumentMetadata, data: InputStream): DocumentKey

    /** Reads document by the given metadata.
     * @param document metadata of the document to be saved including its path and encryption key.
     * @return decrypted document content */
    suspend fun get(document: DocumentMetadata): InputStream

    /** Deletes document with the given metadata. Performs removal of the document from the file system.
     * @param document metadata of the document to be saved including its path.
     * */
    suspend fun delete(document: DocumentMetadata)
}
