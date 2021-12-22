package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.UserIdentifier

fun interface CreateApplicationUseCase {
    suspend operator fun invoke(userIdentifier: UserIdentifier): Application
}

class CreateApplicationUseCaseImpl(
    private val applicationDataSource: ApplicationDataSource,
) : CreateApplicationUseCase {
    override suspend fun invoke(userIdentifier: UserIdentifier): Application {
        return applicationDataSource.createApplication(userIdentifier)
    }
}
