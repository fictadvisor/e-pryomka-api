package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.models.DocumentIdentifier
import com.fictadvisor.pryomka.domain.models.User
import java.io.InputStream

fun interface GetDocumentUseCase {
    suspend operator fun invoke(documentId: DocumentIdentifier): InputStream?
}

class GetDocumentUseCaseImpl(
    private val documentContentDataSource: DocumentContentDataSource,
    private val applicationDataSource: ApplicationDataSource
) : GetDocumentUseCase {
    override suspend fun invoke(documentId: DocumentIdentifier): InputStream? {
        TODO("Not yet implemented")
    }
}
