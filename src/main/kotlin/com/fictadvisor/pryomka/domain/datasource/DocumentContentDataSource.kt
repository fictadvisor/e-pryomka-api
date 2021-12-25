package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.DocumentKey
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import java.io.InputStream

interface DocumentContentDataSource {
    suspend fun save(document: DocumentMetadata, data: InputStream): DocumentKey
    suspend fun get(document: DocumentMetadata): InputStream
    suspend fun delete(document: DocumentMetadata)
}
