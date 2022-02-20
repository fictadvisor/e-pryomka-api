package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.ReviewsDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application.Status

interface ChangeApplicationStatusUseCase {
    suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
        newStatus: Status,
        statusMessage: String?,
    )
}

// Todo add Result class + Domain Exception class with error codes
class ChangeApplicationStatusUseCaseImpl(
    private val userDataSource: UserDataSource,
    private val applicationDataSource: ApplicationDataSource,
    private val reviewsDataSource: ReviewsDataSource,
) : ChangeApplicationStatusUseCase {
    override suspend fun changeStatus(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
        newStatus: Status,
        statusMessage: String?,
    ) {
        val user = resolveUser(userId)
        val application = resolveApplication(user, applicationId).takeIf {
            it.status != newStatus
        } ?: permissionDenied("Can't change to this status")

        val message = statusMessage.takeIf { newStatus == Status.Rejected }

        when {
            user.isEntrant -> changeStatusEntrant(application, newStatus)
            user.isOperator -> changeStatusOperator(application, user.id, newStatus, message)
            user.isAdmin -> changeStatusAdmin(application, user.id, newStatus, message)
        }
    }

    private suspend fun resolveUser(userId: UserIdentifier) = userDataSource.findEntrant(userId)
        ?: userDataSource.findStaff(userId)
        ?: unauthorized()

    private suspend fun resolveApplication(
        user: User,
        applicationId: ApplicationIdentifier,
    ) = if (user.isEntrant) {
        applicationDataSource.get(applicationId, user.id) ?: notfound("Can't find application")
    } else {
        applicationDataSource.getById(applicationId) ?: notfound("Can't find application")
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
        operatorId: UserIdentifier,
        newStatus: Status,
        statusMsg: String?,
    ) {
        when (application.status) {
            Status.Pending -> {
                val alreadyInReview = reviewsDataSource.checkInReview(application.id)
                if (newStatus != Status.InReview || alreadyInReview) {
                    permissionDenied("Can't change to this status")
                }

                reviewsDataSource.addToReview(application.id, operatorId)
            }

            Status.InReview -> {
                val reviewerId = reviewsDataSource.getReviewerId(application.id)
                if (newStatus !in listOf(Status.Approved, Status.Rejected) || operatorId != reviewerId) {
                    permissionDenied("Can't change to this status")
                }

                reviewsDataSource.removeFromReview(application.id)
            }

            else -> permissionDenied("Can't change this status")
        }
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }

    private suspend fun changeStatusAdmin(
        application: Application,
        adminId: UserIdentifier,
        newStatus: Status,
        statusMsg: String?,
    ) {
        when {
            newStatus == Status.InReview -> {
                reviewsDataSource.addToReview(application.id, adminId)
            }
            application.status == Status.InReview -> {
                reviewsDataSource.removeFromReview(application.id)
            }
        }
        applicationDataSource.changeStatus(application.id, newStatus, statusMsg)
    }
}
