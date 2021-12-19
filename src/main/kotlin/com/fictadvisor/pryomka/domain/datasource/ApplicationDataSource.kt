package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.Document
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.UserIdentifier

interface ApplicationDataSource {
    suspend fun getApplication(userId: UserIdentifier): Application
    suspend fun addDocument(
        userId: UserIdentifier,
        document: Document,
        type: DocumentType,
        key: DocumentKey,
    )
}
