package com.fictadvisor.pryomka.domain.datasource

import com.fictadvisor.pryomka.domain.models.Document
import java.io.InputStream
import java.io.OutputStream

interface DocumentDataSource {
    suspend fun saveDocument(document: Document, data: InputStream)
    suspend fun getDocument(document: Document): OutputStream
}
