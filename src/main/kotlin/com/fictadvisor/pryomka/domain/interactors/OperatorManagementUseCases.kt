package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

/** Encapsulates business logic regarding operator management. Used by a system administrator. */
interface OperatorManagementUseCases {
    /** Registers operator with given credentials.
     * @return information of the new operator. */
    suspend fun add(login: String, password: String): User

    /** Returns all registered operators in the system.
     * @return list of operators or empty list */
    suspend fun getAll(): List<User.Staff>

    /** Deletes operator with given id. */
    suspend fun delete(id: UserIdentifier)
}

class OperatorManagementUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val registerStaffUseCase: RegisterStaffUseCase,
) : OperatorManagementUseCases {
    override suspend fun add(login: String, password: String): User {
        val existing = userDataSource.findStaffByCredentials(login)

        if (existing != null) error("User already exists")

        return registerStaffUseCase.register(login, password, User.Staff.Role.Operator)
    }

    override suspend fun getAll(): List<User.Staff> = userDataSource.findAllByRole(
        User.Staff.Role.Operator
    )

    override suspend fun delete(id: UserIdentifier) {
        userDataSource.deleteStaff(id)
    }
}
