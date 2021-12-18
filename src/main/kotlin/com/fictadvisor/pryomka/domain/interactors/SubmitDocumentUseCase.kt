package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.domain.models.Document
import com.fictadvisor.pryomka.domain.models.DocumentType

fun interface SubmitDocumentUseCase {
    suspend operator fun invoke(document: Document, type: DocumentType)
}
