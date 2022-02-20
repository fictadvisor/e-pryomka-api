package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import com.fictadvisor.pryomka.domain.models.generateApplicationId
import com.fictadvisor.pryomka.mock
import com.fictadvisor.pryomka.verify
import com.fictadvisor.pryomka.whenever
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetDocumentUseCaseImplTest {
    private val contentDs: DocumentContentDataSource = mock()
    private val metadataDs: DocumentMetadataDataSource = mock()

    private val useCase = GetDocumentsUseCaseImpl(contentDs, metadataDs)

    @Test
    fun `get document`(): Unit = runBlocking {
        // GIVEN
        val metadata = DocumentMetadata(
            applicationId = generateApplicationId(),
            path = Path("/document.doc"),
            type = DocumentType.Contract,
            key = "abcdefg"
        )
        whenever(metadataDs.find(metadata.applicationId, metadata.type)).thenReturn(metadata)
        whenever(contentDs.get(metadata)).thenReturn(InputStream.nullInputStream())

        // WHEN
        val result = useCase.get(metadata.applicationId, metadata.type)

        // THEN
        assertEquals(metadata, result?.first)
        verify(contentDs).get(metadata)
    }

    @Test
    fun `should return null on get non-existent document`(): Unit = runBlocking {
        // GIVEN
        whenever(metadataDs.find(any(), any())).thenReturn(null)

        // WHEN
        val result = useCase.get(generateApplicationId(), DocumentType.Contract)

        // THEN
        assertNull(result)
    }
}
