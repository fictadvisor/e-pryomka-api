package com.fictadvisor.pryomka

import com.fictadvisor.pryomka.data.datasources.*
import com.fictadvisor.pryomka.data.datasources.faculty.LearningFormatsDataSourceImpl
import com.fictadvisor.pryomka.data.datasources.faculty.SpecialitiesDataSourceImpl
import com.fictadvisor.pryomka.domain.datasource.*
import com.fictadvisor.pryomka.domain.datasource.faculty.LearningFormatsDataSource
import com.fictadvisor.pryomka.domain.datasource.faculty.SpecialitiesDataSource
import com.fictadvisor.pryomka.domain.interactors.*
import com.fictadvisor.pryomka.domain.interactors.faculty.LearningFormatsUseCases
import com.fictadvisor.pryomka.domain.interactors.faculty.LearningFormatsUseCasesImpl
import com.fictadvisor.pryomka.domain.interactors.faculty.SpecialitiesUseCases
import com.fictadvisor.pryomka.domain.interactors.faculty.SpecialitiesUseCasesImpl
import io.ktor.application.*
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

val generalModule = module {
    single<UserDataSource> { UserDataSourceImpl() }
}

val authModule = module {
    single<TokenDataSource> { TokenDataSourceImpl() }
    single<AuthUseCase> { AuthUseCaseImpl(get(), get()) }
}

val adminZoneModule = module {
    single<RegisterStaffUseCase> { RegisterStaffUseCaseImpl(get()) }
    single<OperatorManagementUseCases> { OperatorManagementUseCaseImpl(get(), get()) }
}

val applicationsModule = module {
    single<ApplicationDataSource> { ApplicationDataSourceImpl() }
    single<ApplicationUseCase> { ApplicationUseCaseImpl(get()) }
    single<ReviewsDataSource> { ReviewsDataSourceImpl() }

    single<ChangeApplicationStatusUseCase> { ChangeApplicationStatusUseCaseImpl(get(), get(), get()) }
}

val documentsModule = module {
    single<DocumentMetadataDataSource> { DocumentMetadataDataSourceImpl() }
    single<DocumentContentDataSource> { FsDocumentContentDataSource(Environment.SECRET) }

    single<GetDocumentsUseCase> { GetDocumentsUseCaseImpl(get(), get()) }
    single<SubmitDocumentUseCase> { SubmitDocumentUseCaseImpl(get(), get()) }
}

val facultyModule = module {
    single<LearningFormatsDataSource> { LearningFormatsDataSourceImpl() }
    single<SpecialitiesDataSource> { SpecialitiesDataSourceImpl() }

    single<LearningFormatsUseCases> { LearningFormatsUseCasesImpl(get()) }
    single<SpecialitiesUseCases> { SpecialitiesUseCasesImpl(get(), get()) }
}

fun Application.configureDi() = install(Koin) {
    modules(listOf(
        generalModule,
        authModule,
        adminZoneModule,
        applicationsModule,
        documentsModule,
        facultyModule,
    ))
}
