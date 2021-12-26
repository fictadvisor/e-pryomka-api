package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Reviews
import com.fictadvisor.pryomka.domain.datasource.ReviewsDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ReviewsDataSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ReviewsDataSource {
    override suspend fun addToReview(
        applicationId: ApplicationIdentifier,
        operatorId: UserIdentifier,
    ): Unit = newSuspendedTransaction(dispatcher) {
        Reviews.insert {
            it[Reviews.applicationId] = applicationId.value
            it[Reviews.operatorId] = operatorId.value
        }
    }

    override suspend fun checkInReview(
        applicationId: ApplicationIdentifier,
    ): Boolean = newSuspendedTransaction(dispatcher) {
        Reviews.select { Reviews.applicationId eq applicationId.value }.limit(1).firstOrNull() != null
    }

    override suspend fun getReviewerId(
        applicationId: ApplicationIdentifier,
    ): UserIdentifier? = newSuspendedTransaction(dispatcher) {
        Reviews.select { Reviews.applicationId eq applicationId.value }
            .limit(1)
            .firstOrNull()
            ?.let { UserIdentifier(it[Reviews.operatorId]) }
    }

    override suspend fun removeFromReview(
        applicationId: ApplicationIdentifier,
    ): Unit = newSuspendedTransaction(dispatcher) {
        Reviews.deleteWhere { Reviews.applicationId eq applicationId.value }
    }
}
