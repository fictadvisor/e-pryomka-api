package com.fictadvisor.pryomka.domain.models

/** Type of document. */
enum class DocumentType {
    /** Photo or a scan-copy of the passport (either a book or an ID-card). */
    Passport,

    /** Photo of an entrant. */
    Photo,

    /** Signed and filled studying contract. */
    Contract;

    companion object {
        /** Parses document type from the string description ignoring case.
         * @return type parsed or null if string doesn't match any type. */
        fun fromString(value: String): DocumentType? {
            return when {
                value.equals("passport", true) -> Passport
                value.equals("photo", true) -> Photo
                value.equals("contract", true) -> Contract
                else -> null
            }
        }
    }
}
