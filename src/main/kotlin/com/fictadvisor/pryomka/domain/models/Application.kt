package com.fictadvisor.pryomka.domain.models

data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: Set<DocumentType>,
    val status: Status,
) {
    enum class Status {
        Preparing,
        Pending,
        Approved,
        Rejected,
        Cancelled,
    }

    operator fun plus(documents: Set<DocumentType>) = copy(
        documents = this.documents + documents
    )
}
