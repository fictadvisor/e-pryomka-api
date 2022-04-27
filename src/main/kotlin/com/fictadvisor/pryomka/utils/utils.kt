package com.fictadvisor.pryomka.utils

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.util.*

/** Parses user id from request's principals.
 * @return parsed identifier or null if parsing has failed for any reason. */
val ApplicationCall.userId: UserIdentifier? get() = principal<JWTPrincipal>()
    ?.payload
    ?.getClaim("user_id")
    ?.asString()
    ?.toUserIdentifierOrNull()

/** Generates unique path in [Environment.UPLOADS_DIR] for given file to store.
 * @param fileName name of the document to store
 * @param type type of document to store. It will be added to document name
 * @return path where document can be stored. */
fun Application.pathFor(fileName: String, type: DocumentType) = Path(
    "${Environment.UPLOADS_DIR}/${id.value}/${type}_$fileName"
)

/** Tries to parse UUID from given string and returns null in case of exception. */
fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: Exception) {
    null
}
