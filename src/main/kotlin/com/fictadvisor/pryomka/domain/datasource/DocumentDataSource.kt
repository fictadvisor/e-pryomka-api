package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Document
import java.io.InputStream

typealias DocumentKey = String
interface DocumentDataSource {
    suspend fun saveDocument(document: Document, data: InputStream): DocumentKey
    suspend fun getDocument(document: Document, key: DocumentKey): InputStream
}
