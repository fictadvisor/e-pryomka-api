package com.fictadvisor.pryomka.domain.models

data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: List<DocumentType>,
    val status: Status,
    val statusMsg: String? = null,
) {
    enum class Status {
        Preparing,
        Pending,
        Approved,
        Rejected,
        Cancelled;

        val isTerminal get() = this != Preparing && this != Pending
    }

    operator fun plus(documents: List<DocumentType>) = copy(
        documents = documents
    )
}
