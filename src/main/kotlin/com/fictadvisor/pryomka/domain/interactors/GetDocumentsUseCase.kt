package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentType
import java.io.InputStream

interface GetDocumentsUseCase {
    suspend fun get(applicationId: ApplicationIdentifier, type: DocumentType) : InputStream
    suspend fun getAll(applicationId: ApplicationIdentifier)
}

class GetDocumentsUseCaseImpl(
    private val documentsContentDS: DocumentContentDataSource,
    private val documentsMetadataDS: DocumentMetadataDataSource,
) : GetDocumentsUseCase {
    override suspend fun get(applicationId: ApplicationIdentifier, type: DocumentType) : InputStream {
        val metadata = documentsMetadataDS.find(applicationId, type)!!
        val content = documentsContentDS.get(metadata)

        return content
    }

    override suspend fun getAll(applicationId: ApplicationIdentifier) {
        TODO("Not yet implemented")
    }
}
