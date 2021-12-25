package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class DocumentMetadataDataSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DocumentMetadataDataSource {
    override suspend fun add(
        document: DocumentMetadata
    ): Unit = newSuspendedTransaction(dispatcher) {
        Documents.insert {
            it[Documents.applicationId] = document.applicationId.value
            it[path] = document.path.value
            it[Documents.type] = document.type
            it[Documents.key] = document.key
        }
    }

    override suspend fun replace(
        document: DocumentMetadata
    ): Unit = newSuspendedTransaction(dispatcher) {
        Documents.update(
            where = {
                Documents.applicationId.eq(document.applicationId.value).and(
                    Documents.type.eq(document.type)
                )
            }
        ) {
            it[Documents.key] = document.key
            it[Documents.path] = document.path.value
        }
    }

    override suspend fun find(
        applicationId: ApplicationIdentifier,
        type: DocumentType,
    ): DocumentMetadata? = newSuspendedTransaction(dispatcher) {
        Documents.select {
            Documents.applicationId.eq(applicationId.value) and Documents.type.eq(type)
        }.limit(1)
            .map {
                DocumentMetadata(
                    applicationId = applicationId,
                    path = Path(it[Documents.path]),
                    type = type,
                    key = it[Documents.key]
                )
            }.firstOrNull()
    }
}
