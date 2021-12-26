package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.datasources.*
import com.fictadvisor.pryomka.domain.datasource.*
import com.fictadvisor.pryomka.domain.interactors.*

object Provider {
    val applicationUseCase: ApplicationUseCase by lazy {
        ApplicationUseCaseImpl(applicationDataSource)
    }

    val changeApplicationStatusUseCase: ChangeApplicationStatusUseCase by lazy {
        ChangeApplicationStatusUseCaseImpl(userDataSource, applicationDataSource, reviewsDataSource)
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
    private val reviewsDataSource: ReviewsDataSource by lazy { ReviewsDataSourceImpl() }
}
