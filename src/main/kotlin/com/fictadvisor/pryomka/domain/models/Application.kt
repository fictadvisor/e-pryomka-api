package com.fictadvisor.pryomka.domain.models

data class Application(
    val userId: UserIdentifier,
    val documents: Map<DocumentType, Document>,
)
