package com.fictadvisor.pryomka.domain.models

import kotlinx.datetime.Instant

data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: List<DocumentType>,
    val speciality: Speciality,
    val funding: Funding,
    val learningFormat: LearningFormat,
    val createdAt: Instant,
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

    enum class Speciality {
        SPEC_121,
        SPEC_123,
        SPEC_126,
    }

    enum class Funding {
        Budget,
        Contract,
    }

    enum class LearningFormat {
        FullTime,
        PartTime,
    }

    operator fun plus(documents: List<DocumentType>) = copy(
        documents = documents,
    )
}
