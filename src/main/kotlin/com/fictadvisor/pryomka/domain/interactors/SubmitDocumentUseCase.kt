package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import java.io.InputStream

fun interface SubmitDocumentUseCase {
    suspend operator fun invoke(
        document: DocumentMetadata,
        content: InputStream,
    )
}

class SubmitDocumentUseCaseImpl(
    private val contentDataSource: DocumentContentDataSource,
    private val metadataDataSource: DocumentMetadataDataSource,
) : SubmitDocumentUseCase {
    override suspend fun invoke(
        document: DocumentMetadata,
        content: InputStream
    ) {
        val key = contentDataSource.save(document, content)
        saveMetadata(document.copy(key = key))
    }

    private suspend fun saveMetadata(document: DocumentMetadata) {
        val existingDoc = metadataDataSource.find(
            document.applicationId,
            document.type,
        )

        if (existingDoc == null) {
            metadataDataSource.add(document)
        } else {
            metadataDataSource.replace(document)

            /** Don't delete existing document if it has same path,
             *  because it was already overwritten. */
            if (existingDoc.path != document.path) contentDataSource.delete(existingDoc)
        }
    }
}
