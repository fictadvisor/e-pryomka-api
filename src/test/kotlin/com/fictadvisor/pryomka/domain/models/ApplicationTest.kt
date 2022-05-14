package com.fictadvisor.pryomka.domain.models

import com.fictadvisor.pryomka.domain.models.faculty.LearningFormat
import com.fictadvisor.pryomka.domain.models.faculty.Speciality
import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ApplicationTest {
    private val fullTime = LearningFormat(generateLearningFormatId(), "full time studying")
    private val spec121 = Speciality(121, "Software Engineering")

    @Test
    fun `test application plus with empty docs`() {
        // GIVEN
        val application = Application(
            id = ApplicationIdentifier(UUID(0, 0)),
            userId = UserIdentifier(UUID(0, 0)),
            documents = setOf(),
            speciality = spec121,
            funding = Application.Funding.Budget,
            learningFormat = fullTime,
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
            speciality = spec121,
            funding = Application.Funding.Budget,
            learningFormat = fullTime,
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
