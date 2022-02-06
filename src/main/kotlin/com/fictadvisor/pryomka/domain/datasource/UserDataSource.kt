package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface UserDataSource {
    suspend fun findEntrant(id: UserIdentifier): User?

    suspend fun findStaff(
        id: UserIdentifier,
        roles: List<User.Role> = listOf(
            User.Role.Admin,
            User.Role.Operator,
        ),
    ): User?

    suspend fun findStaffByCredentials(login: String, password: String? = null): User?

    suspend fun findAllByRole(role: User.Role): List<User>

    suspend fun registerStaff(login: String, password: String, role: User.Role)

    suspend fun deleteStaff(id: UserIdentifier)
}
