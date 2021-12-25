package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application.Status

interface ChangeApplicationStatusUseCase {
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
        newStatus: Status,
        statusMsg: String?,
    )
}

// Todo add Result class + Domain Exception class with error codes
class ChangeApplicationStatusUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val applicationDataSource: ApplicationDataSource,
) : ChangeApplicationStatusUseCase {
    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
        newStatus: Status,
        statusMsg: String?,
    ) {
        val user = userDataSource.findUser(userId) ?: unauthorized()
        val application = applicationDataSource.get(applicationId, userId) ?: notfound("Can't find application")
        val msg = statusMsg.takeIf { newStatus == Status.Rejected }

        when (user.role) {
            User.Role.Entrant -> changeStatusEntrant(application, newStatus)
            User.Role.Operator -> changeStatusOperator(application, newStatus, msg)
            User.Role.Admin -> changeStatusAdmin(application, newStatus, msg)
        }
    }

    private suspend fun changeStatusEntrant(
        application: Application,
        newStatus: Status,
    ) {
        when (application.status) {
            Status.Preparing -> {
                if (newStatus !in listOf(Status.Pending, Status.Cancelled)) {
                    permissionDenied("Can't change to this status")
                }
            }

            Status.Pending -> {
                if (newStatus != Status.Cancelled) {
                    permissionDenied("Can't change to this status")
                }
            }

            else -> permissionDenied("Can't change this status")
        }

        applicationDataSource.changeStatus(application.id, newStatus, null)
    }

    private suspend fun changeStatusOperator(
        application: Application,
        newStatus: Status,
        statusMsg: String?,
    ) {
        when (application.status) {
            Status.Pending -> {
                if (newStatus != Status.InReview) {
                    permissionDenied("Can't change to this status")
                }
            }

            Status.InReview -> {
                if (newStatus !in listOf(Status.Approved, Status.Rejected)) {
                    permissionDenied("Can't change to this status")
                }
            }

            else -> permissionDenied("Can't change this status")
        }
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }

    private suspend fun changeStatusAdmin(
        application: Application,
        newStatus: Status,
        statusMsg: String?,
    ) {
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }
}
