package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import com.fictadvisor.pryomka.domain.models.duplicate
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

interface ApplicationUseCase {
    suspend fun getByUserId(userId: UserIdentifier): List<Application>
    suspend fun getById(applicationId: ApplicationIdentifier): Application?
    suspend fun get(applicationId: ApplicationIdentifier, userId: UserIdentifier): Application?
    suspend fun create(application: Application, userId: UserIdentifier)
    suspend fun getAll(): List<Application>
}

class ApplicationUseCaseImpl(
    private val ds: ApplicationDataSource,
) : ApplicationUseCase {
    override suspend fun get(
        applicationId: ApplicationIdentifier,
        userId: UserIdentifier,
    ) = ds.get(applicationId, userId)
    override suspend fun getByUserId(userId: UserIdentifier) = ds.getByUserId(userId)
    override suspend fun getById(applicationId: ApplicationIdentifier): Application? = ds.getById(applicationId)
    override suspend fun getAll() = ds.getAll()
    override suspend fun create(application: Application, userId: UserIdentifier) {
        ds.getByUserId(userId).filter { !it.status.isNegativelyTerminated }.takeIf { nonTerminated ->
            val a = nonTerminated.none {
                it.funding == application.funding &&
                it.learningFormat == application.learningFormat &&
                it.speciality == application.speciality
            }
            a
        } ?: duplicate("Can't duplicate applications")

        ds.create(application)
    }
}
