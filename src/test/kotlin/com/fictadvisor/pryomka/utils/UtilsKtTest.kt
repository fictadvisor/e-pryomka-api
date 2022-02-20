package com.fictadvisor.pryomka.utils

import com.fictadvisor.pryomka.Environment
import com.fictadvisor.pryomka.domain.models.Application
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.UserIdentifier
import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class UtilsKtTest {
    @Test
    fun `test pathFor`() {
        // GIVEN
        val application = Application(
            id = ApplicationIdentifier(UUID(0, 0)),
            userId = UserIdentifier(UUID(0, 0)),
            documents = setOf(),
            speciality = Application.Speciality.SPEC_121,
            funding = Application.Funding.Budget,
            learningFormat = Application.LearningFormat.FullTime,
            createdAt = Clock.System.now(),
            status = Application.Status.Preparing
        )

        // WHEN
        val path = application.pathFor("passport.png", DocumentType.Passport).value
        val (baseDir, applicationDir, fileName) = path.split('/')

        // THEN
        assertEquals(Environment.UPLOADS_DIR, baseDir)
        assertEquals(application.id.value.toString(), applicationDir)
        assertEquals("Passport_passport.png", fileName)
    }

    @Test
    fun `test String toUUIDOrNull - general case`() {
        // GIVEN
        val string = "b7ca4bcb-766f-4554-b30b-2950fffaad1b"

        // WHEN+THEN
        assertEquals(
            UUID.fromString("b7ca4bcb-766f-4554-b30b-2950fffaad1b"),
            string.toUUIDOrNull()
        )
    }

    @Test
    fun `test String toUUIDOrNull - invalid string`() {
        // GIVEN
        val string = "oh no"

        // WHEN+THEN
        assertNull(string.toUUIDOrNull())
    }
}
