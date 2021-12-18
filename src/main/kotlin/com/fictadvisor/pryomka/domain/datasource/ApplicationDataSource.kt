package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ApplicationDataSource {
    suspend fun getApplication(userIdentifier: UserIdentifier): Application
    suspend fun setApplication(application: Application)
}
