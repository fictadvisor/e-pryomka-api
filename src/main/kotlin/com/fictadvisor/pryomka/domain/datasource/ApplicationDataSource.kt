package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

/** DataSource that provides methods for managing application. These methods can be used by all types of users.  */
interface ApplicationDataSource {
    /** Obtains [Application] of the given entrant.
     * @return application or null if it was not found */
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?

    /** Obtains all [Application]s of given user.
     * @return list of applications or empty list. */
    suspend fun getByUserId(userId: UserIdentifier): List<Application>

    /** Obtains [Application] with given [ApplicationIdentifier].
     * @return application or null if it was not found */
    suspend fun getById(applicationId: ApplicationIdentifier): Application?

    /** Returns list of all application in the system. */
    suspend fun getAll(): List<Application>

    /** Creates new application. */
    suspend fun create(application: Application)

    /** Updates status of the given application. */
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        status: Application.Status,
        statusMessage: String?,
    )
}
