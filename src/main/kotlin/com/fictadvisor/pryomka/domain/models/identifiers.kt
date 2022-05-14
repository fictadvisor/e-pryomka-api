package com.fictadvisor.pryomka.domain.models

import java.util.*

/** User's unique identifier. Represented by a random UUID value. */
data class UserIdentifier(val value: UUID)

/** Document unique identifier. Represented by a random UUID value. */
data class DocumentIdentifier(val value: UUID)

/** Application unique identifier. Represented by a random UUID value. */
data class ApplicationIdentifier(val value: UUID)

/** Learning Format unique identifier. Represented by a random UUID value. */
data class LearningFormatIdentifier(val value: UUID)

/** Document encryption key used to access document content. */
typealias DocumentKey = String

/** Generates new random [UserIdentifier]. */
fun generateUserId() = UserIdentifier(UUID.randomUUID())

/** Generates new random [DocumentIdentifier]. */
fun generateDocumentId() = DocumentIdentifier(UUID.randomUUID())

/** Generates new random [ApplicationIdentifier]. */
fun generateApplicationId() = ApplicationIdentifier(UUID.randomUUID())

/** Generates new random [LearningFormatIdentifier]. */
fun generateLearningFormatId() = LearningFormatIdentifier(UUID.randomUUID())

/** Parses [UserIdentifier] from the string.
 * @return parsed identifier
 * @throws IllegalArgumentException */
fun String.toUserIdentifier() = UserIdentifier(UUID.fromString(this))

/** Parses [DocumentIdentifier] from the string.
 * @return parsed identifier
 * @throws IllegalArgumentException */
fun String.toDocumentIdentifier() = DocumentIdentifier(UUID.fromString(this))

/** Parses [ApplicationIdentifier] from the string.
 * @return parsed identifier
 * @throws IllegalArgumentException */
fun String.toApplicationIdentifier() = ApplicationIdentifier(UUID.fromString(this))

/** Parses [LearningFormatIdentifier] from the string.
 * @return parsed identifier
 * @throws IllegalArgumentException */
fun String.toLearningFormatIdentifier() = LearningFormatIdentifier(UUID.fromString(this))

/** Parses [UserIdentifier] from the string.
 * @return parsed identifier or null if exception has occurred */
fun String.toUserIdentifierOrNull() = runCatching { toUserIdentifier() }.getOrNull()

/** Parses [DocumentIdentifier] from the string.
 * @return parsed identifier or null if exception has occurred */
fun String.toDocumentIdentifierOrNull() = runCatching { toDocumentIdentifier() }.getOrNull()

/** Parses [ApplicationIdentifier] from the string.
 * @return parsed identifier or null if exception has occurred */
fun String.toApplicationIdentifierOrNull() = runCatching { toApplicationIdentifier() }.getOrNull()

/** Parses [LearningFormatIdentifier] from the string.
 * @return parsed identifier or null if exception has occurred */
fun String.toLearningFormatIdentifierOrNull() = runCatching { toLearningFormatIdentifier() }.getOrNull()
