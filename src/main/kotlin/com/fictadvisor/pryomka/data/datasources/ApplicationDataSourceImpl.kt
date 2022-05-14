package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.data.db.LearningFormats
import com.fictadvisor.pryomka.data.db.Specialities
import com.fictadvisor.pryomka.data.mappers.toApplication
import com.fictadvisor.pryomka.data.mappers.toDocumentMetadata
import com.fictadvisor.pryomka.data.mappers.toLearningFormat
import com.fictadvisor.pryomka.data.mappers.toSpeciality
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class ApplicationDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : ApplicationDataSource {
    override suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications
                .innerJoin(LearningFormats)
                .innerJoin(Specialities)
                .select {
                    (Applications.id eq applicationId.value) and (Applications.userId eq userId.value)
                }.limit(1)
                .map {
                    val speciality = it.toSpeciality()
                    val learningFormat = it.toLearningFormat()

                    it.toApplication(speciality, learningFormat)
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
            Applications
                .innerJoin(LearningFormats)
                .innerJoin(Specialities)
                .select { Applications.userId eq userId.value }
                .map {
                    val speciality = it.toSpeciality()
                    val learningFormat = it.toLearningFormat()

                    it.toApplication(speciality, learningFormat)
                }
        }.takeIf { it.isNotEmpty() } ?: return emptyList()

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId inList applications.map { it.id.value } }
                .map(ResultRow::toDocumentMetadata)
        }

        val map = documents.groupBy { it.applicationId }

        return applications.map { application ->
            application.copy(documents = map[application.id]?.map { it.type }?.toSet() ?: emptySet())
        }
    }

    override suspend fun getById(applicationId: ApplicationIdentifier): Application? {
        val application = newSuspendedTransaction(dispatchers) {
            Applications
                .innerJoin(LearningFormats)
                .innerJoin(Specialities)
                .select { Applications.id eq applicationId.value }
                .limit(1)
                .map {
                    val speciality = it.toSpeciality()
                    val learningFormat = it.toLearningFormat()

                    it.toApplication(speciality, learningFormat)
                }
                .firstOrNull()
        } ?: return null

        val documents = newSuspendedTransaction(dispatchers) {
            Documents.select { Documents.applicationId eq application.id.value }
                .map { it[Documents.type] }
        }

        return application + documents.toSet()
    }

    override suspend fun getAll(): List<Application> {

        val applicationsDef = suspendedTransactionAsync(dispatchers) {
            Applications
                .innerJoin(LearningFormats)
                .innerJoin(Specialities)
                .selectAll()
                .map {
                    val speciality = it.toSpeciality()
                    val learningFormat = it.toLearningFormat()

                    it.toApplication(speciality, learningFormat)
                }
        }

        val documentsDef = suspendedTransactionAsync(dispatchers) {
            Documents.selectAll().map(ResultRow::toDocumentMetadata)
        }

        val map = documentsDef.await().groupBy { it.applicationId }

        return applicationsDef.await().map { application ->
            application.copy(documents = map[application.id]?.map { it.type }?.toSet() ?: emptySet())
        }
    }

    override suspend fun create(
        application: Application,
    ) {
        val (specialityId, learningFormatId) = coroutineScope {
            val speciality = suspendedTransactionAsync(dispatchers) {
                Specialities.select { Specialities.code eq application.speciality.code }
                    .limit(1)
                    .map { it[Specialities.id] }
                    .first()
            }

            val learningFormat = suspendedTransactionAsync(dispatchers) {
                LearningFormats.select { LearningFormats.id eq application.learningFormat.id.value }
                    .limit(1)
                    .map { it[LearningFormats.id] }
                    .first()
            }

            speciality.await() to learningFormat.await()
        }

        newSuspendedTransaction(dispatchers) {
            Applications.insert {
                it[id] = application.id.value
                it[userId] = application.userId.value
                it[speciality] = specialityId
                it[funding] = application.funding
                it[learningFormat] = learningFormatId
                it[createdAt] = application.createdAt.toJavaInstant()
                it[status] = application.status
                it[statusMessage] = application.statusMessage
            }
        }
    }

    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        status: Application.Status,
        statusMessage: String?,
    ): Unit = newSuspendedTransaction(dispatchers) {
        Applications.update(
            where = { Applications.id eq applicationId.value }
        ) {
            it[Applications.status] = status
            it[Applications.statusMessage] = statusMessage
        }
    }
}
