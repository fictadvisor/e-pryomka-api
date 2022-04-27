package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

/** Manages reviews of the applications by admins and operators. */
interface ReviewsDataSource {
    /** Marks given application as being reviewed by the given operator. */
    suspend fun addToReview(applicationId: ApplicationIdentifier, operatorId: UserIdentifier)

    /** Checks if application is marked as being reviewed
     * @return true if someone is reviewing application, false otherwise */
    suspend fun checkInReview(applicationId: ApplicationIdentifier): Boolean

    /** Returns identifier of the operator or admin that is reviewing application. */
    suspend fun getReviewerId(applicationId: ApplicationIdentifier): UserIdentifier?

    /** Marks given application as not being reviewed anymore. */
    suspend fun removeFromReview(applicationId: ApplicationIdentifier)
}
