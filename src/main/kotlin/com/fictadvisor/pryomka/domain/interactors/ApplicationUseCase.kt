package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import com.fictadvisor.pryomka.domain.models.duplicate

/** Encapsulates business logic related to application management. */
interface ApplicationUseCase {
    /** Searches all applications of a specific user.
     * @return list of applications or empty list. */
    suspend fun getByUserId(userId: UserIdentifier): List<Application>

    /** Searches application with specified application id.
     * @return application or null. */
    suspend fun getById(applicationId: ApplicationIdentifier): Application?

    /** Searches application with specified application id belonging to a given user.
     * @return application or null. */
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?

    /** Creates application with a given data that belongs to a given user */
    suspend fun create(application: Application, userId: UserIdentifier)

    /** Returns all applications in the system. */
    suspend fun getAll(): List<Application>
}

class ApplicationUseCaseImpl(
    private val ds: ApplicationDataSource,
) : ApplicationUseCase {
    override suspend fun get(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
    ) = ds.get(applicationId, userId)
    override suspend fun getByUserId(userId: UserIdentifier) = ds.getByUserId(userId)
    override suspend fun getById(applicationId: ApplicationIdentifier): Application? = ds.getById(applicationId)
    override suspend fun getAll() = ds.getAll()
    override suspend fun create(application: Application, userId: UserIdentifier) {
        ds.getByUserId(userId).filter { !it.status.isNegativelyTerminated }.takeIf { nonTerminated ->
            nonTerminated.none {
                it.funding == application.funding &&
                it.learningFormat == application.learningFormat &&
                it.speciality == application.speciality
            }
        } ?: duplicate("Can't duplicate applications")

        ds.create(application)
    }
}
