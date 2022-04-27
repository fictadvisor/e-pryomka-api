package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.datasources.*
import com.fictadvisor.pryomka.domain.datasource.*
import com.fictadvisor.pryomka.domain.interactors.*

/** Object that plays role of dependency locator.
 * TODO: replace with some DI framework. */
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

    val operatorManagementUseCases: OperatorManagementUseCases by lazy {
        OperatorManagementUseCaseImpl(userDataSource, registerStaffUseCase)
    }

    val authUseCase: AuthUseCase by lazy {
        AuthUseCaseImpl(userDataSource, tokenDataSource)
    }

    val registerStaffUseCase: RegisterStaffUseCase by lazy {
        RegisterStaffUseCaseImpl(userDataSource)
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

    private val tokenDataSource: TokenDataSource by lazy { TokenDataSourceImpl() }
}
