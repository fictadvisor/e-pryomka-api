package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.*

interface ChangeApplicationStatusUseCase {
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userIdentifier: UserIdentifier,
        newStatus: Application.Status,
        statusMsg: String?,
    )
}

class ChangeApplicationStatusUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val applicationDataSource: ApplicationDataSource,
) {
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userIdentifier: UserIdentifier,
        newStatus: Application.Status,
        statusMsg: String?,
    ) {
        val user = userDataSource.findUser(userIdentifier) ?: error("")
        val application = applicationDataSource.getById(applicationId) ?: error("")
        val msg = statusMsg.takeIf { newStatus == Application.Status.Rejected }

        if (!user.role.canApply(newStatus)) error("")

        when (user.role) {
            User.Role.Entrant -> changeStatusEntrant(application, newStatus)
            User.Role.Operator -> changeStatusOperator(application, newStatus, msg)
            User.Role.Admin -> changeStatusAdmin(application, newStatus, msg)
        }
    }

    private suspend fun changeStatusEntrant(
        application: Application,
        newStatus: Application.Status,
    ) {
        if (application.status.isTerminal) error("")
        applicationDataSource.changeStatus(application.id, newStatus, null)
    }

    private suspend fun changeStatusOperator(
        application: Application,
        newStatus: Application.Status,
        statusMsg: String?,
    ) {
        if (application.status != Application.Status.Pending) error("")
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }

    private suspend fun changeStatusAdmin(
        application: Application,
        newStatus: Application.Status,
        statusMsg: String?,
    ) {
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }
}
