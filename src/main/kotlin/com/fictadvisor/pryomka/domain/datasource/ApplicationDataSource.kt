package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.*

interface ApplicationDataSource {
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?
    suspend fun getByUserId(userId: UserIdentifier): List<Application>
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun getAll(): List<Application>
    suspend fun create(userId: UserIdentifier): Application
    suspend fun changeStatus(applicationId: ApplicationIdentifier, status: Application.Status, statusMsg: String?)
}
