package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import com.fictadvisor.pryomka.domain.models.generateUserId

interface OperatorManagementUseCases {
    suspend fun add(name: String)
    suspend fun getAll(): List<User>
    suspend fun delete(id: UserIdentifier)
}

class OperatorManagementUseCaseImpl(
    private val userDataSource: UserDataSource,
) : OperatorManagementUseCases {
    override suspend fun add(name: String) {
        val existing = userDataSource.findUser(name)

        if (existing != null) error("User already exists")

        userDataSource.addUser(User(
            id = generateUserId(),
            name = name,
            role = User.Role.Operator,
        ))
    }

    override suspend fun getAll(): List<User> = userDataSource.findByRole(User.Role.Operator)

    override suspend fun delete(id: UserIdentifier) {
        val user = userDataSource.findUser(id)
            ?.takeIf { it.role == User.Role.Operator }
            ?: error("User does not exist")

        userDataSource.deleteUser(user)
    }
}
