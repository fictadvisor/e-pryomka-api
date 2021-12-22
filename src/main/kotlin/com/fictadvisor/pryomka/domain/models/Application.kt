package com.fictadvisor.pryomka.domain.models

data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: List<DocumentType>,
    val status: Status,
) {
    enum class Status {
        Preparing,
        Pending,
        Approved,
        Rejected,
        Cancelled,
    }

    operator fun plus(documents: List<DocumentType>) = copy(
        documents = documents
    )
}
