package com.fictadvisor.pryomka.domain.interactors

import com.fictadvisor.pryomka.any
import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.datasource.DocumentMetadataDataSource
import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class SubmitDocumentUseCaseImplTest {
    private val contentDS = Mockito.mock(DocumentContentDataSource::class.java)
    private val metadataDS = Mockito.mock(DocumentMetadataDataSource::class.java)
    private val useCase = SubmitDocumentUseCaseImpl(contentDS, metadataDS)

    @BeforeTest
    fun init(): Unit = runBlocking {
        Mockito.`when`(
            contentDS.save(any(), any())
        ).thenReturn("SECRET_KEY")

        Mockito.`when`(metadataDS.add(any())).thenReturn(Unit)
    }

    @Test
    fun `test document content saved`(): Unit = runBlocking {
        // GIVEN
        val documentMeta = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("document.txt"),
            type = DocumentType.Contract,
            key = "",
        )

        val documentContent = """
            I am sorry, but I cannot be friends with a goddess,
            because I have signed a contract with a devil.            
        """.trimIndent().byteInputStream()

        // WHEN
        useCase(documentMeta, documentContent)

        // THEN
        Mockito.verify(contentDS, times(1))
            .save(documentMeta, documentContent)
    }

    @Test
    fun `add new document`() = runBlocking {
        // GIVEN
        val documentMeta = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("document.txt"),
            type = DocumentType.Contract,
            key = "",
        )

        val documentContent = """
            A life that lives without doing anything is the same as a slow death.
        """.trimIndent().byteInputStream()

        // WHEN
        useCase(documentMeta, documentContent)

        // THEN
        Mockito.verify(metadataDS, times(1))
            .add(documentMeta.copy(key = "SECRET_KEY"))

        return@runBlocking
    }

    @Test
    fun `replace document, don't delete old one`() = runBlocking {
        // GIVEN
        val documentMeta = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("document.txt"),
            type = DocumentType.Contract,
            key = "",
        )

        val documentContent = """
            There is no such thing as objective information.
            Zero, in the end, journalism is the product of a human mind.
        """.trimIndent().byteInputStream()

        Mockito.`when`(
            metadataDS.find(documentMeta.applicationId, documentMeta.type)
        ).thenReturn(documentMeta)

        // WHEN
        useCase(documentMeta, documentContent)

        // THEN
        Mockito.verify(metadataDS, times(1))
            .replace(documentMeta.copy(key = "SECRET_KEY"))

        Mockito.verify(contentDS, times(0))
            .delete(any())

        return@runBlocking
    }

    @Test
    fun `replace document, delete old one`() = runBlocking {
        // GIVEN
        val documentMeta = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("document.txt"),
            type = DocumentType.Contract,
            key = "",
        )

        val oldDocMeta = documentMeta.copy(path = Path("old.txt"))

        val documentContent = """
            The human heart is the source of all our power.
            We fight with the power of our hearts.
        """.trimIndent().byteInputStream()

        Mockito.`when`(
            metadataDS.find(documentMeta.applicationId, documentMeta.type)
        ).thenReturn(oldDocMeta)

        // WHEN
        useCase(documentMeta, documentContent)

        // THEN
        Mockito.verify(metadataDS, times(1))
            .replace(documentMeta.copy(key = "SECRET_KEY"))

        Mockito.verify(contentDS, times(1))
            .delete(any())

        return@runBlocking
    }
}
