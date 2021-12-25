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
    val applicationUseCase: ApplicationUseCase by lazy {
        ApplicationUseCaseImpl(applicationDataSource)
    }

    val changeApplicationStatusUseCase: ChangeApplicationStatusUseCase by lazy {
        ChangeApplicationStatusUseCaseImpl(userDataSource, applicationDataSource)
    }

    val getDocumentsUseCase: GetDocumentsUseCase by lazy {
        GetDocumentsUseCaseImpl(documentContentDataSource, documentMetadataDataSource)
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

    val operatorManagementUseCases: OperatorManagementUseCases by lazy {
        OperatorManagementUseCaseImpl(userDataSource)
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
