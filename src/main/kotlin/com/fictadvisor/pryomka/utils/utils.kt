package com.fictadvisor.pryomka.utils

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.models.Path
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import io.ktor.application.*
import io.ktor.auth.*
import java.util.*

val ApplicationCall.userId: UserIdentifier? get() = principal<UserIdPrincipal>()?.name
    ?.let(UUID::fromString)
    ?.let(::UserIdentifier)

fun pathFor(userId: UserIdentifier, fileName: String) = Path(
    "${Environment.UPLOADS_DIR}/${userId.value}/$fileName"
)
