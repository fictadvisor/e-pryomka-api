package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.models.User

fun interface LogInUseCase {
    suspend operator fun invoke(credentials: String): User
}
