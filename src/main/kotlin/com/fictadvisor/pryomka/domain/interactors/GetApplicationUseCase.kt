package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.UserIdentifier

fun interface GetApplicationUseCase {
    suspend operator fun invoke(userIdentifier: UserIdentifier): Application
}
