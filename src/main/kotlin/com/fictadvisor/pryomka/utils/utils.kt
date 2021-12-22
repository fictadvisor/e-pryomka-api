package com.fictadvisor.pryomka.utils

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import io.ktor.application.*
import io.ktor.auth.*
import java.util.*

val ApplicationCall.userId: UserIdentifier? get() = principal<UserIdPrincipal>()?.name
    ?.let(UUID::fromString)
    ?.let(::UserIdentifier)

fun Application.pathFor(fileName: String, type: DocumentType) = Path(
    "${Environment.UPLOADS_DIR}/${id.value}/${type}_$fileName"
)
