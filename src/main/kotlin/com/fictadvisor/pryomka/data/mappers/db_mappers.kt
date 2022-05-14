package com.fictadvisor.pryomka.data.mappers

import com.fictadvisor.pryomka.data.db.*
import com.fictadvisor.pryomka.domain.models.*
import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toApplication(speciality: Speciality, learningFormat: LearningFormat) = Application(
    id = ApplicationIdentifier(this[Applications.id]),
    userId = UserIdentifier(this[Applications.userId]),
    documents = setOf(),
    speciality = speciality,
    funding = this[Applications.funding],
    learningFormat = learningFormat,
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

fun ResultRow.toLearningFormat() = LearningFormat(
    id = LearningFormatIdentifier(this[LearningFormats.id]),
    name = this[LearningFormats.name]
)

fun ResultRow.toSpeciality() = Speciality(
    code = this[Specialities.code],
    name = this[Specialities.name]
)
