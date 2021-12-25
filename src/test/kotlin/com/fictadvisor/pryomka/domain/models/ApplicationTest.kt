package com.fictadvisor.pryomka.domain.models

import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ApplicationTest {
    @Test
    fun `test application plus with empty docs`() {
        // GIVEN
        val application = Application(
            id = ApplicationIdentifier(UUID(0, 0)),
            userId = UserIdentifier(UUID(0, 0)),
            documents = setOf(),
            speciality = Application.Speciality.SPEC_121,
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            createdAt = Clock.System.now(),
            status = Application.Status.Approved,
        )

        // WHEN
        val newApplication = application + setOf(DocumentType.Passport, DocumentType.Contract)

        // THEN
        assertEquals(application.id, newApplication.id)
        assertEquals(application.userId, newApplication.userId)
        assertEquals(application.status, newApplication.status)
        assertEquals(setOf(DocumentType.Passport, DocumentType.Contract), newApplication.documents)
    }

    @Test
    fun `test application plus with non-empty docs`() {
        // GIVEN
        val application = Application(
            id = ApplicationIdentifier(UUID(0, 0)),
            userId = UserIdentifier(UUID(0, 0)),
            documents = setOf(DocumentType.Passport),
            speciality = Application.Speciality.SPEC_121,
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            createdAt = Clock.System.now(),
            status = Application.Status.Approved,
        )

        // WHEN
        val newApplication = application + setOf(DocumentType.Photo, DocumentType.Contract)

        // THEN
        assertEquals(application.id, newApplication.id)
        assertEquals(application.userId, newApplication.userId)
        assertEquals(application.status, newApplication.status)
        assertEquals(
            setOf(
                DocumentType.Passport,
                DocumentType.Contract,
                DocumentType.Photo,
            ),
            newApplication.documents
        )
    }
}
