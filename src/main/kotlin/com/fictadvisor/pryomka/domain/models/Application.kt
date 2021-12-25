package com.fictadvisor.pryomka.domain.models

import kotlinx.datetime.Instant

data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: Set<DocumentType>,
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
        InReview,
        Approved,
        Rejected,
        Cancelled;

        val isNegativelyTerminated get() = this == Cancelled || this == Rejected
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

    operator fun plus(documents: Set<DocumentType>) = copy(
        documents = this.documents + documents
    )
}
