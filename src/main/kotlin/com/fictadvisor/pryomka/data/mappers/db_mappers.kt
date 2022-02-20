package com.fictadvisor.pryomka.data.mappers

import com.fictadvisor.pryomka.data.db.Applications
import com.fictadvisor.pryomka.data.db.Documents
import com.fictadvisor.pryomka.domain.models.*
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toApplication() = Application(
    id = ApplicationIdentifier(this[Applications.id]),
    userId = UserIdentifier(this[Applications.userId]),
    documents = setOf(),
    speciality = this[Applications.speciality],
    funding = this[Applications.funding],
    learningFormat = this[Applications.learningFormat],
    createdAt = this[Applications.createdAt].toKotlinInstant(),
    status = this[Applications.status],
    statusMessage = this[Applications.statusMessage],
)

fun ResultRow.toDocumentMetadata() = DocumentMetadata(
    applicationId = ApplicationIdentifier(this[Documents.applicationId]),
    path = Path(this[Documents.path]),
    type = this[Documents.type],
    key = this[Documents.key],
)
