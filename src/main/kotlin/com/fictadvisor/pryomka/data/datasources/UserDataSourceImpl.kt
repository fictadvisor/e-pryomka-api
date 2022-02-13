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
    ): User? = newSuspendedTransaction(dispatchers) {
        Entrants.select { Entrants.id eq id.value }
            .singleOrNull()
            ?.let { User(id, it[Entrants.name], User.Role.Entrant) }
    }

    override suspend fun findStaff(
        id: UserIdentifier,
        roles: List<User.Role>,
    ): User? = newSuspendedTransaction(dispatchers) {
        Staff.select {
            Staff.id.eq(id.value).and {
                Staff.role.inList(roles)
            }
        }.singleOrNull()
            ?.let { User(id, it[Staff.login], it[Staff.role]) }
    }

    override suspend fun findStaffByCredentials(
        login: String,
        password: String?,
    ): User? = newSuspendedTransaction(dispatchers) {
        Staff.select {
            Staff.login.eq(login)
        }.singleOrNull()?.let {
            val hashedPassword = it[Staff.password]
            val salt = it[Staff.salt]

            if (password == null || Hash.verify(password, hashedPassword, salt)) {
                User(UserIdentifier(it[Staff.id]), login, it[Staff.role])
            } else {
                null
            }
        }
    }

    override suspend fun findAllByRole(
        role: User.Role
    ): List<User> = newSuspendedTransaction(dispatchers) {
        if (role == User.Role.Entrant) {
            Entrants.selectAll().map {
                User(
                    UserIdentifier(it[Entrants.id]),
                    it[Entrants.name],
                    role,
                )
            }
        } else {
            Staff.select { Staff.role eq role }
                .map {
                    User(
                        UserIdentifier(it[Staff.id]),
                        it[Staff.login],
                        role,
                    )
                }
        }
    }

    override suspend fun registerStaff(
        login: String,
        password: String,
        role: User.Role,
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
            Staff.id.eq(id.value).andNot { Staff.role eq User.Role.Admin }
        }
    }
}
