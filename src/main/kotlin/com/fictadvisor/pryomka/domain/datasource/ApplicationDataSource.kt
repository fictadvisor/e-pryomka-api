package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.*

interface ApplicationDataSource {
    suspend fun getByUserId(userId: UserIdentifier): Application?
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun getAll(): List<Application>
    suspend fun create(userId: UserIdentifier): Application
}
