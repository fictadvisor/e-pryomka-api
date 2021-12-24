package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.util.*

class ApplicationDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : ApplicationDataSource {
    override suspend fun getByUserId(userId: UserIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications.select { Applications.userId eq userId.value }
                .limit(1)
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = userId,
                        documents = listOf(),
                        status = it[Applications.status],
                    )
                }.firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents
    }

    override suspend fun getById(applicationId: ApplicationIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications.select { Applications.id eq applicationId.value }
                .limit(1)
                .map {
                    Application(
                        id = applicationId,
                        userId = UserIdentifier(it[Applications.userId]),
                        documents = listOf(),
                        status = it[Applications.status],
                    )
                }.firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents
    }

    override suspend fun getAll(): List<Application> {

        val applicationsDef = suspendedTransactionAsync(dispatchers) {
            Applications.selectAll()
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = UserIdentifier(it[Applications.userId]),
                        documents = listOf(),
                        status = it[Applications.status],
                    )
                }
        }

        val documentsDef = suspendedTransactionAsync(dispatchers) {
            Documents.select { Documents.type eq DocumentType.Photo }
                .map {
                    DocumentMetadata(
                        applicationId = ApplicationIdentifier(it[Documents.applicationId]),
                        path = Path(it[Documents.path]),
                        type = it[Documents.type],
                        key = it[Documents.key],
                    )
                }
        }

        val map = documentsDef.await().groupBy { it.applicationId }

        return applicationsDef.await().map { application ->
            application.copy(documents = map[application.id]?.map { it.type } ?: emptyList())
        }
    }

    override suspend fun create(
        userId: UserIdentifier
    ): Application = newSuspendedTransaction(dispatchers) {
        val application = Application(
            id = ApplicationIdentifier(UUID.randomUUID()),
            userId = userId,
            documents = listOf(),
            status = Application.Status.Pending
        )

        Applications.insert {
            it[Applications.id] = application.id.value
            it[Applications.userId] = application.userId.value
            it[Applications.status] = application.status
        }

        application
    }

    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        status: Application.Status,
        statusMsg: String?,
    ) {

    }
}
