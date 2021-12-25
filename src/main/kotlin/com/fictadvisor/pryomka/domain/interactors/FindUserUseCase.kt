package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface FindUserUseCase {
    suspend fun findByName(name: String): User?
    suspend fun findById(id: UserIdentifier): User?
}

class FindUserUseCaseImpl(
    private val userDataSource: UserDataSource,
) : FindUserUseCase {
    override suspend fun findByName(name: String): User? = userDataSource.findUser(name)
    override suspend fun findById(id: UserIdentifier): User? = userDataSource.findUser(id)
}
