package com.fictadvisor.pryomka.utils

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.Application
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.util.*

val ApplicationCall.userId: UserIdentifier? get() = principal<JWTPrincipal>()
    ?.payload
    ?.getClaim("user_id")
    ?.asString()
    ?.toUserIdentifierOrNull()

fun Application.pathFor(fileName: String, type: DocumentType) = Path(
    "${Environment.UPLOADS_DIR}/${id.value}/${type}_$fileName"
)

fun String.toUUIDOrNull() = try {
    UUID.fromString(this)
} catch (e: Exception) {
    null
}
