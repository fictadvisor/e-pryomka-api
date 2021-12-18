package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.data.upsert
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ApplicationDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : ApplicationDataSource {
    override suspend fun getApplication(userId: UserIdentifier): Application = withContext(dispatchers) {
        transaction {
            val documents = mutableMapOf<DocumentType, Document>()
            val query = Documents.select { Documents.userId eq userId.value }

            query.forEach { row ->
                val path = Path(row[Documents.path])
                val doc = Document(path)
                val type = row[Documents.type]

                documents[type] = doc
            }

            Application(userId, documents)
        }
    }

    override suspend fun addDocument(
        userId: UserIdentifier,
        document: Document,
        type: DocumentType,
    ): Unit = withContext(dispatchers) {
        transaction {
            Documents.upsert {
                it[Documents.userId] = userId.value
                it[Documents.path] = document.path.value
                it[Documents.type] = type
            }
        }
    }
}
