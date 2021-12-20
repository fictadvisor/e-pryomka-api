package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.data.datasources.ApplicationDataSourceImpl
import com.fictadvisor.pryomka.data.datasources.FsDocumentDataSource
import com.fictadvisor.pryomka.data.datasources.UserDataSourceImpl
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.*

object Provider {
    val getApplicationUseCase: GetApplicationUseCase by lazy {
        GetApplicationUseCaseImpl(applicationDataSource)
    }

    val submitDocumentUseCase: SubmitDocumentUseCase by lazy {
        SubmitDocumentUseCaseImpl(applicationDataSource, documentDataSource)
    }

    val createUserUseCase: CreateUserUseCase by lazy {
        CreateUserUseCaseImpl(userDataSource)
    }

    val findUserUseCase: FindUserUseCase by lazy {
        FindUserUseCaseImpl(userDataSource)
    }

    private val userDataSource: UserDataSource by lazy { UserDataSourceImpl() }
    private val applicationDataSource: ApplicationDataSource by lazy { ApplicationDataSourceImpl() }
    private val documentDataSource: DocumentDataSource by lazy {
        FsDocumentDataSource(System.getenv("SECRET"))
    }
}
