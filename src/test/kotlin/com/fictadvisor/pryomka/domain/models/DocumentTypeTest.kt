package com.fictadvisor.pryomka.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentTypeTest {
    @Test
    fun `test type from string - all lowercase`() {
        // GIVEN
        val type = "passport"

        // WHEN+THEN
        assertEquals(
            DocumentType.Passport,
            DocumentType.fromString(type)
        )
    }

    @Test
    fun `test type from string - camel lowercase`() {
        // GIVEN
        val type = "Photo"

        // WHEN+THEN
        assertEquals(
            DocumentType.Photo,
            DocumentType.fromString(type)
        )
    }

    @Test
    fun `test type from string - mixed case`() {
        // GIVEN
        val type = "cOntRaCT"

        // WHEN+THEN
        assertEquals(
            DocumentType.Contract,
            DocumentType.fromString(type)
        )
    }

    @Test
    fun `test type from string - invalid type`() {
        // GIVEN
        val type = "not a document"

        // WHEN+THEN
        assertNull(DocumentType.fromString(type))
    }
}