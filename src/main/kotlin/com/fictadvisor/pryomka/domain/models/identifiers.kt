package com.fictadvisor.pryomka.domain.models

import java.util.*

data class UserIdentifier(val value: UUID)
data class DocumentIdentifier(val value: UUID)
data class ApplicationIdentifier(val value: UUID)
typealias DocumentKey = String

fun generateUserId() = UserIdentifier(UUID.randomUUID())
fun generateDocumentId() = DocumentIdentifier(UUID.randomUUID())
fun generateApplicationId() = ApplicationIdentifier(UUID.randomUUID())

fun String.toUserIdentifier() = UserIdentifier(UUID.fromString(this))
fun String.toDocumentIdentifier() = DocumentIdentifier(UUID.fromString(this))
fun String.toApplicationIdentifier() = ApplicationIdentifier(UUID.fromString(this))

fun String.toUserIdentifierOrNull() = runCatching { toUserIdentifier() }.getOrNull()
fun String.toDocumentIdentifierOrNull() = runCatching { toDocumentIdentifier() }.getOrNull()
fun String.toApplicationIdentifierOrNull() = runCatching { toApplicationIdentifier() }.getOrNull()
