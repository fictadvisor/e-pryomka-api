package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.UserIdentifier

fun interface GetApplicationUseCase {
    suspend operator fun invoke(userIdentifier: UserIdentifier): Application
}

class GetApplicationUseCaseImpl(
    private val ds: ApplicationDataSource,
) : GetApplicationUseCase {
    override suspend fun invoke(userIdentifier: UserIdentifier) = ds.getApplication(userIdentifier)
}
