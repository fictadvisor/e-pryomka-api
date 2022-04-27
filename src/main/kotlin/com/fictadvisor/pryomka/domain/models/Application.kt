package com.fictadvisor.pryomka.domain.models

import kotlinx.datetime.Instant

/** Represents entrant's application. */
data class Application(
    val id: ApplicationIdentifier,
    val userId: UserIdentifier,
    val documents: Set<DocumentType>,
    val speciality: Speciality,
    val funding: Funding,
    val learningFormat: LearningFormat,
    val createdAt: Instant,
    val status: Status,
    val statusMessage: String? = null,
) {
    /** Represents status which application is in. */
    enum class Status {
        /** Entrant is still adding new documents and filling application.
         * It cannot be reviewed yet. But entrant can decide to delete this application. */
        Preparing,

        /** Entrant has prepared all necessary documents. Application can be taken by one of the operators into review.
         * Entrant still can delete the application but can no longer modify it. */
        Pending,

        /** Operator has taken this application into review and checks documents. */
        InReview,

        /** Application has been approved by the operator. Entrant will study in FICT! */
        Approved,

        /** Application has some flaws or is incomplete. Entrant need to submit a new one. */
        Rejected,

        /** Entrant has decided to cancel application and so it can no longer be neither modified nor reviewed. */
        Cancelled;

        /**
         * @return true if application is either Cancelled or Rejected (i.e. cannot be modified anymore) and false otherwise. */
        val isNegativelyTerminated get() = this == Cancelled || this == Rejected
    }

    /** Represents a speciality of FICT which application belongs to. */
    enum class Speciality {
        /** 121 - Software Engineering */
        SPEC_121,

        /** 123 - Computer Engineering */
        SPEC_123,

        /** 126 - Information System And Technologies */
        SPEC_126,
    }

    /** Represents a type of tuition which application belongs to. */
    enum class Funding {
        /** Studying is funded by the government of Ukraine */
        Budget,

        /** Studying is funded by a private individual or a private enterprise. */
        Contract,
    }

    /** Represents a type of tuition which application belongs to. */
    enum class LearningFormat {
        /** Student studies in campus in a common manner. */
        FullTime,

        /** Student studies at home and only visits campus for a certification. */
        PartTime,
    }

    /** Appends set this application documents with the new ones.
     * @return new [Application] instance with updated list of documents. */
    operator fun plus(documents: Set<DocumentType>) = copy(
        documents = this.documents + documents
    )
}
