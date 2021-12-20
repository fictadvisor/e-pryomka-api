package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import java.util.*

fun interface CreateUserUseCase {
    suspend operator fun invoke(credentials: String): User
}

class CreateUserUseCaseImpl(
    private val userDataSource: UserDataSource,
) : CreateUserUseCase {
    override suspend fun invoke(credentials: String): User {
        val user = User(UserIdentifier(UUID.randomUUID()), credentials, User.Role.Entrant)
        userDataSource.addUser(user)
        return user
    }
}
