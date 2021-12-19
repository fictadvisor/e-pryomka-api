package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentDataSource
import com.fictadvisor.pryomka.domain.models.Document
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import java.io.InputStream

fun interface SubmitDocumentUseCase {
    suspend operator fun invoke(
        userIdentifier: UserIdentifier,
        document: Document,
        type: DocumentType,
        content: InputStream,
    )
}

class SubmitDocumentUseCaseImpl(
    private val applicationDataSource: ApplicationDataSource,
    private val documentDataSource: DocumentDataSource,
) : SubmitDocumentUseCase {
    override suspend fun invoke(
        userIdentifier: UserIdentifier,
        document: Document,
        type: DocumentType,
        content: InputStream
    ) {
        val key = documentDataSource.saveDocument(document, content)
        applicationDataSource.addDocument(userIdentifier, document, type, key)
    }
}
