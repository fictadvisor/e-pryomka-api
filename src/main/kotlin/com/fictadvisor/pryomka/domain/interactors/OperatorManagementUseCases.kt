package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface OperatorManagementUseCases {
    suspend fun add(login: String, password: String): User
    suspend fun getAll(): List<User>
    suspend fun delete(id: UserIdentifier)
}

class OperatorManagementUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val registerStaffUseCase: RegisterStaffUseCase,
) : OperatorManagementUseCases {
    override suspend fun add(login: String, password: String): User {
        val existing = userDataSource.findStaffByCredentials(login)

        if (existing != null) error("User already exists")

        return registerStaffUseCase.register(login, password, User.Role.Operator)
    }

    override suspend fun getAll(): List<User> = userDataSource.findAllByRole(User.Role.Operator)

    override suspend fun delete(id: UserIdentifier) {
        userDataSource.deleteStaff(id)
    }
}
