package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.*

interface ApplicationDataSource {
    suspend fun getApplication(userId: UserIdentifier): Application?
    suspend fun createApplication(userId: UserIdentifier): Application
}
