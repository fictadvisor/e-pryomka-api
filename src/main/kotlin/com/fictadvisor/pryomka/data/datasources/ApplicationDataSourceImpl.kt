package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class ApplicationDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : ApplicationDataSource {
    override suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications.select { (Applications.id eq applicationId.value) and (Applications.userId eq userId.value) }
                .limit(1)
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = userId,
                        documents = setOf(),
                        speciality = it[Applications.speciality],
                        funding = it[Applications.funding],
                        learningFormat = it[Applications.learningFormat],
                        createdAt = it[Applications.createdAt].toKotlinInstant(),
                        status = it[Applications.status],
                        statusMsg = it[Applications.statusMsg],
                    )
                }.firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents.toSet()
    }

    override suspend fun getByUserId(userId: UserIdentifier): List<Application> {
        val applications = newSuspendedTransaction(dispatchers) {
            Applications.select { Applications.userId eq userId.value }
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = userId,
                        documents = setOf(),
                        speciality = it[Applications.speciality],
                        funding = it[Applications.funding],
                        learningFormat = it[Applications.learningFormat],
                        createdAt = it[Applications.createdAt].toKotlinInstant(),
                        status = it[Applications.status],
                        statusMsg = it[Applications.statusMsg],
                    )
                }
        }.takeIf { it.isNotEmpty() } ?: return emptyList()

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId inList applications.map { it.id.value } }
                .map {
                    DocumentMetadata(
                        applicationId = ApplicationIdentifier(it[Documents.applicationId]),
                        path = Path(it[Documents.path]),
                        type = it[Documents.type],
                        key = it[Documents.key],
                    )
                }
        }

        val map = documents.groupBy { it.applicationId }

        return applications.map { application ->
            application.copy(documents = map[application.id]?.map { it.type }?.toSet() ?: emptySet())
        }
    }

    override suspend fun getById(applicationId: ApplicationIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications.select { Applications.id eq applicationId.value }
                .limit(1)
                .map {
                    Application(
                        id = applicationId,
                        userId = UserIdentifier(it[Applications.userId]),
                        documents = setOf(),
                        speciality = it[Applications.speciality],
                        funding = it[Applications.funding],
                        learningFormat = it[Applications.learningFormat],
                        createdAt = it[Applications.createdAt].toKotlinInstant(),
                        status = it[Applications.status],
                        statusMsg = it[Applications.statusMsg],
                    )
                }.firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents.toSet()
    }

    override suspend fun getAll(): List<Application> {

        val applicationsDef = suspendedTransactionAsync(dispatchers) {
            Applications.selectAll()
                .map {
                    Application(
                        id = ApplicationIdentifier(it[Applications.id]),
                        userId = UserIdentifier(it[Applications.userId]),
                        documents = setOf(),
                        speciality = it[Applications.speciality],
                        funding = it[Applications.funding],
                        learningFormat = it[Applications.learningFormat],
                        createdAt = it[Applications.createdAt].toKotlinInstant(),
                        status = it[Applications.status],
                        statusMsg = it[Applications.statusMsg],
                    )
                }
        }

        val documentsDef = suspendedTransactionAsync(dispatchers) {
            Documents.selectAll()
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
            application.copy(documents = map[application.id]?.map { it.type }?.toSet() ?: emptySet())
        }
    }

    override suspend fun create(
        application: Application,
    ): Unit = newSuspendedTransaction(dispatchers) {

        Applications.insert {
            it[Applications.id] = application.id.value
            it[Applications.userId] = application.userId.value
            it[Applications.speciality] = application.speciality
            it[Applications.funding] = application.funding
            it[Applications.learningFormat] = application.learningFormat
            it[Applications.createdAt] = application.createdAt.toJavaInstant()
            it[Applications.status] = application.status
            it[Applications.statusMsg] = application.statusMsg
        }
    }

    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        status: Application.Status,
        statusMsg: String?,
    ): Unit = newSuspendedTransaction(dispatchers) {
        Applications.update(
            where = { Applications.id eq applicationId.value }
        ) {
            it[Applications.status] = status
            it[Applications.statusMsg] = statusMsg
        }
    }
}
