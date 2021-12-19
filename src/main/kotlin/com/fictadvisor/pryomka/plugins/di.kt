package com.fictadvisor.pryomka.plugins

import com.fictadvisor.pryomka.data.datasources.ApplicationDataSourceImpl
import com.fictadvisor.pryomka.data.datasources.FsDocumentDataSource
import com.fictadvisor.pryomka.domain.datasource.ApplicationDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentDataSource
import com.fictadvisor.pryomka.domain.interactors.GetApplicationUseCase
import com.fictadvisor.pryomka.domain.interactors.GetApplicationUseCaseImpl
import com.fictadvisor.pryomka.domain.interactors.SubmitDocumentUseCase
import com.fictadvisor.pryomka.domain.interactors.SubmitDocumentUseCaseImpl

object Provider {
    val getApplicationUseCase: GetApplicationUseCase by lazy {
        GetApplicationUseCaseImpl(applicationDataSource)
    }

    val submitDocumentUseCase: SubmitDocumentUseCase by lazy {
        SubmitDocumentUseCaseImpl(applicationDataSource, documentDataSource)
    }

    private val applicationDataSource: ApplicationDataSource by lazy { ApplicationDataSourceImpl() }
    private val documentDataSource: DocumentDataSource by lazy {
        FsDocumentDataSource(System.getenv("SECRET"))
    }
}
