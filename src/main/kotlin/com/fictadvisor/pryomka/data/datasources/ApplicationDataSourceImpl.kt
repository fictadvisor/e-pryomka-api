package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class ApplicationDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : ApplicationDataSource {
    override suspend fun getApplication(userId: UserIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications.select { Applications.userId eq userId.value }
                .limit(1)
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = userId,
                        documents = listOf(),
                        status = it[Applications.status]
                    )
                }.firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents
    }

    override suspend fun createApplication(
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
}
