package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.datasources.ApplicationDataSourceImpl
import com.fictadvisor.pryomka.data.datasources.DocumentMetadataDataSourceImpl
import com.fictadvisor.pryomka.data.datasources.FsDocumentContentDataSource
import com.fictadvisor.pryomka.data.datasources.UserDataSourceImpl
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.datasource.UserDataSource
import com.fictadvisor.pryomka.domain.interactors.*

object Provider {
    val getApplicationUseCase: GetApplicationUseCase by lazy {
        GetApplicationUseCaseImpl(applicationDataSource)
    }

    val createApplicationUseCase: CreateApplicationUseCase by lazy {
        CreateApplicationUseCaseImpl(applicationDataSource)
    }

    val submitDocumentUseCase: SubmitDocumentUseCase by lazy {
        SubmitDocumentUseCaseImpl(documentContentDataSource, documentMetadataDataSource)
    }

    val createUserUseCase: CreateUserUseCase by lazy {
        CreateUserUseCaseImpl(userDataSource)
    }

    val findUserUseCase: FindUserUseCase by lazy {
        FindUserUseCaseImpl(userDataSource)
    }

    private val userDataSource: UserDataSource by lazy { UserDataSourceImpl() }
    private val applicationDataSource: ApplicationDataSource by lazy { ApplicationDataSourceImpl() }
    private val documentContentDataSource: DocumentContentDataSource by lazy {
        FsDocumentContentDataSource(Environment.SECRET)
    }
    private val documentMetadataDataSource: DocumentMetadataDataSource by lazy {
        DocumentMetadataDataSourceImpl()
    }
}
