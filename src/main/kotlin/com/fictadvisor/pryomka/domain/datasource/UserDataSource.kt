package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface UserDataSource {
    suspend fun findEntrant(id: UserIdentifier): User.Entrant?

    suspend fun findEntrantByTelegramId(id: Long): User.Entrant?

    suspend fun registerEntrant(user: User.Entrant)

    suspend fun updateEntrant(user: User.Entrant)

    suspend fun findStaff(
        id: UserIdentifier,
        roles: List<User.Staff.Role> = User.Staff.Role.values().toList(),
    ): User.Staff?

    suspend fun findStaffByCredentials(login: String, password: String? = null): User.Staff?

    suspend fun findAllByRole(role: User.Staff.Role): List<User.Staff>

    suspend fun registerStaff(login: String, password: String, role: User.Staff.Role)

    suspend fun deleteStaff(id: UserIdentifier)
}
