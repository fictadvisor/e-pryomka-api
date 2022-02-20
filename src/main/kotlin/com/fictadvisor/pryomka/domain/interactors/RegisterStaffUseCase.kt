package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.User

interface RegisterStaffUseCase {
    suspend fun register(
        login: String,
        password: String,
        role: User.Staff.Role,
    ): User.Staff
}

class RegisterStaffUseCaseImpl(
    private val userDataSource: UserDataSource
) : RegisterStaffUseCase {
    override suspend fun register(login: String, password: String, role: User.Staff.Role): User.Staff {
        if (role == User.Staff.Role.Admin) {
            val admins = userDataSource.findAllByRole(User.Staff.Role.Admin)

            if (admins.isNotEmpty()) error("Admin already exists")
        }

        userDataSource.registerStaff(login, password, role)
        return userDataSource.findStaffByCredentials(login, password) ?: error("Failed to register user")
    }
}
