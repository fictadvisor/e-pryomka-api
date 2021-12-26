package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ReviewsDataSource {
    suspend fun addToReview(applicationId: ApplicationIdentifier, operatorId: UserIdentifier)
    suspend fun checkInReview(applicationId: ApplicationIdentifier): Boolean
    suspend fun getReviewerId(applicationId: ApplicationIdentifier): UserIdentifier?
    suspend fun removeFromReview(applicationId: ApplicationIdentifier)
}
