package com.fictadvisor.pryomka.domain.models

enum class DocumentType {
    Passport,
    Photo,
    Contract;

    companion object {
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
