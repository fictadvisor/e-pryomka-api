package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Users
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataSourceImpl(
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : UserDataSource {
    override suspend fun findUser(id: UserIdentifier): User = withContext(dispatchers) {
        transaction {
            val resultRow = Users.select { Users.id eq id.value }.single()
            User(
                UserIdentifier(resultRow[Users.id]),
                resultRow[Users.name],
                resultRow[Users.role],
            )
        }
    }

    override suspend fun addUser(user: User): Unit = withContext(dispatchers) {
        transaction {
            Users.insert {
                it[id] = user.id.value
                it[name] = user.name
                it[role] = user.role
            }
        }
    }
}
