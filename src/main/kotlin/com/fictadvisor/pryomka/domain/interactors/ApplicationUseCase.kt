package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ApplicationUseCase {
    suspend fun getByUserId(userIdentifier: UserIdentifier): List<Application>
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?
    suspend fun create(application: Application)
    suspend fun getAll(): List<Application>
}

class ApplicationUseCaseImpl(
    private val ds: ApplicationDataSource,
) : ApplicationUseCase {
    override suspend fun get(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
    ) = ds.get(applicationId, userId)
    override suspend fun getByUserId(userIdentifier: UserIdentifier) = ds.getByUserId(userIdentifier)
    override suspend fun getById(applicationId: ApplicationIdentifier): Application? = ds.getById(applicationId)
    override suspend fun getAll() = ds.getAll()
    override suspend fun create(application: Application) = ds.create(application)
}
