package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ApplicationDataSource {
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?
    suspend fun getByUserId(userId: UserIdentifier): List<Application>
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun getAll(): List<Application>
    suspend fun create(application: Application)
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        status: Application.Status,
        statusMessage: String?,
    )
}
