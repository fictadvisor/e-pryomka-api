package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Entrants
import com.fictadvisor.pryomka.data.db.Staff
import com.fictadvisor.pryomka.data.encryption.Hash
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : UserDataSource {
    override suspend fun findEntrant(
        id: UserIdentifier
    ): User.Entrant? = newSuspendedTransaction(dispatchers) {
        Entrants.select { Entrants.id eq id.value }
            .singleOrNull()
            ?.let {
                User.Entrant(
                    id = id,
                    telegramId = it[Entrants.telegramId],
                    firstName = it[Entrants.firstName],
                    lastName = it[Entrants.lastName],
                    userName = it[Entrants.userName],
                    photoUrl = it[Entrants.photoUrl],
                )
            }
    }

    override suspend fun findEntrantByTelegramId(
        id: Long
    ): User.Entrant? = newSuspendedTransaction(dispatchers) {
        Entrants.select { Entrants.telegramId eq id }
            .firstOrNull()
            ?.let {
                User.Entrant(
                    id = UserIdentifier(it[Entrants.id]),
                    telegramId = it[Entrants.telegramId],
                    firstName = it[Entrants.firstName],
                    lastName = it[Entrants.lastName],
                    userName = it[Entrants.userName],
                    photoUrl = it[Entrants.photoUrl],
                )
            }
    }

    override suspend fun registerEntrant(
        user: User.Entrant
    ): Unit = newSuspendedTransaction(dispatchers) {
        Entrants.insert {
            it[id] = user.id.value
            it[telegramId] = user.telegramId
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[userName] = user.userName
            it[photoUrl] = user.photoUrl
        }
    }

    override suspend fun updateEntrant(
        user: User.Entrant
    ): Unit = newSuspendedTransaction(dispatchers) {
        Entrants.update(
            where = { Entrants.telegramId eq user.telegramId }
        ) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[userName] = user.userName
            it[photoUrl] = user.photoUrl
        }
    }

    override suspend fun findStaff(
        id: UserIdentifier,
        roles: List<User.Staff.Role>,
    ): User.Staff? = newSuspendedTransaction(dispatchers) {
        Staff.select {
            Staff.id.eq(id.value).and {
                Staff.role.inList(roles)
            }
        }.singleOrNull()?.let {
            User.Staff(
                id = id,
                name = it[Staff.login],
                role = it[Staff.role]
            )
        }
    }

    override suspend fun findStaffByCredentials(
        login: String,
        password: String?,
    ): User.Staff? = newSuspendedTransaction(dispatchers) {
        Staff.select {
            Staff.login.eq(login)
        }.singleOrNull()?.let {
            val hashedPassword = it[Staff.password]
            val salt = it[Staff.salt]

            if (password == null || Hash.verify(password, hashedPassword, salt)) {
                User.Staff(
                    id = UserIdentifier(it[Staff.id]),
                    name = login,
                    role = it[Staff.role]
                )
            } else {
                null
            }
        }
    }

    override suspend fun findAllByRole(
        role: User.Staff.Role
    ): List<User.Staff> = newSuspendedTransaction(dispatchers) {
        Staff.select { Staff.role eq role }.map {
            User.Staff(
                id = UserIdentifier(it[Staff.id]),
                name = it[Staff.login],
                role = role,
            )
        }
    }

    override suspend fun registerStaff(
        login: String,
        password: String,
        role: User.Staff.Role,
    ): Unit = newSuspendedTransaction(dispatchers) {
        val salt = Hash.generateSalt()
        val hashedPassword = Hash.hash(password, salt)

        Staff.insert {
            it[Staff.login] = login
            it[Staff.password] = hashedPassword
            it[Staff.salt] = salt
            it[Staff.role] = role
        }
    }

    override suspend fun deleteStaff(
        id: UserIdentifier
    ): Unit = newSuspendedTransaction(dispatchers) {
        Staff.deleteWhere {
            Staff.id.eq(id.value).andNot { Staff.role eq User.Staff.Role.Admin }
        }
    }
}
