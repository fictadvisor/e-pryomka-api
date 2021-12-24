package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ApplicationUseCase {
    suspend fun getByUserId(userIdentifier: UserIdentifier): Application?
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun create(userIdentifier: UserIdentifier): Application
    suspend fun getAll(): List<Application>

}

class ApplicationUseCaseImpl(
    private val ds: ApplicationDataSource,
) : ApplicationUseCase {
    override suspend fun getByUserId(userIdentifier: UserIdentifier) = ds.getByUserId(userIdentifier)
    override suspend fun getById(applicationId: ApplicationIdentifier): Application? = ds.getById(applicationId)
    override suspend fun getAll() = ds.getAll()
    override suspend fun create(userIdentifier: UserIdentifier) = ds.create(userIdentifier)
    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userIdentifier: UserIdentifier,
        status: Application.Status,
        statusMsg: String?,
    ): Boolean {

    }
}
