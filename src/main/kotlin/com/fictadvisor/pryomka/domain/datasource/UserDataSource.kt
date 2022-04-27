package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

/** Provides methods to manage users (both staff and entrants). */
interface UserDataSource {
    /** Searches entrant by theirs id.
     * @return entrant or null if nothing was found */
    suspend fun findEntrant(id: UserIdentifier): User.Entrant?

    /** Searches entrant by theirs telegram.
     * @return entrant or null if nothing was found */
    suspend fun findEntrantByTelegramId(id: Long): User.Entrant?

    /** Registers new entrant in the system. */
    suspend fun registerEntrant(user: User.Entrant)

    /** Updates information about entrant by its id. */
    suspend fun updateEntrant(user: User.Entrant)

    /** Searches staff with given id and role
     * @return staff or null if nothing was found */
    suspend fun findStaff(
        id: UserIdentifier,
        roles: List<User.Staff.Role> = User.Staff.Role.values().toList(),
    ): User.Staff?

    /** Searches staff by theirs access credentials. Used for login logic.
     * @return staff or null if nothing was found */
    suspend fun findStaffByCredentials(login: String, password: String? = null): User.Staff?

    /** Searches all staff with given role.
     * @return list of staff or empty list */
    suspend fun findAllByRole(role: User.Staff.Role): List<User.Staff>

    /** Registers staff with given credentials and role in the system. */
    suspend fun registerStaff(login: String, password: String, role: User.Staff.Role)

    /** Deletes staff with given id from the system. */
    suspend fun deleteStaff(id: UserIdentifier)
}
