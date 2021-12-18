package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface UserDataSource {
    suspend fun findUser(identifier: UserIdentifier): User
    suspend fun addUser(user: User)
}
