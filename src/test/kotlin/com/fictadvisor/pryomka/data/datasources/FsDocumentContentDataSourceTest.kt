package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.domain.models.ApplicationIdentifier
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import com.fictadvisor.pryomka.domain.models.DocumentType
import com.fictadvisor.pryomka.domain.models.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.jupiter.api.assertThrows
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FsDocumentContentDataSourceTest {
    @Rule
    @JvmField
    var uploadsDir = TemporaryFolder()

    private val uploadsDirPath get() = uploadsDir.root.path
    private val secret = "LdJQGDpI80BJKqb8EUXeNJtlPoj4m/j+gAzT/gx1coQ="

    @Test
    fun `test save and get`() = runBlocking {
        // GIVEN
        val content = """
            A Life That Lives Without Doing Anything Is The Same As A Slow Death.
        """.trimIndent()

        val metadata = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("$uploadsDirPath/file.txt"),
            type = DocumentType.Passport,
            key = ""
        )

        val ds = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)

        // WHEN
        val key = ds.save(metadata, content.byteInputStream())
        val decoded = String(ds.get(metadata.copy(key = key)).readAllBytes())

        // THEN
        assertEquals(content, decoded)
    }

    @Test
    fun `test save creates file`() = runBlocking {
        // GIVEN
        val content = """
            If The King Doesn’t Move, Then His Subjects Won’t Follow.
        """.trimIndent()

        val metadata = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("$uploadsDirPath/file.txt"),
            type = DocumentType.Contract,
            key = ""
        )

        val ds = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)

        // WHEN
        ds.save(metadata, content.byteInputStream())
        val file = File(metadata.path.value)

        // THEN
        assertTrue(file.exists())
    }

    @Test
    fun `test file content is not plaintext`() = runBlocking {
        // GIVEN
        val content = """
            In their heart, everyone has faith that their victory exists.
            However, in the face of time and destiny, the act of faith is fruitless
            and fleeting at best.
        """.trimIndent()

        val metadata = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("$uploadsDirPath/file.txt"),
            type = DocumentType.Contract,
            key = ""
        )

        val ds = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)

        // WHEN
        ds.save(metadata, content.byteInputStream())
        val file = File(metadata.path.value)

        // THEN
        assertFalse(file.readText().contains(content))
    }

    @Test
    fun `test file cannot be read with different key`(): Unit = runBlocking {
        // GIVEN
        val content = """
            When there is evil in this world that justice cannot defeat,
            would you taint your hands with evil to defeat evil?
            Or would you remain steadfast and righteous even if it means surrendering to evil?
        """.trimIndent()

        val metadata = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("$uploadsDirPath/file.txt"),
            type = DocumentType.Contract,
            key = ""
        )

        val ds = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)

        // WHEN+THEN
        ds.save(metadata, content.byteInputStream())
        assertThrows<Exception> {
            ds.get(metadata.copy(
                // Random key
                key = "EOgJ+z4QIA6SfDE2E8F82oOlLm1g02N0FS39jHEyXg0Jka2vG7DPSIC5JTo6A1nV/ayFNm+HFFvgLKbHeWq2zW0=")
            )
        }
    }

    @Test
    fun `test file can be decrypted with another ds instance`(): Unit = runBlocking {
        // GIVEN
        val content = """
            In this world, evil can arise from the best of intentions.
            And there is good which can come from evil intentions.
        """.trimIndent()

        val metadata = DocumentMetadata(
            applicationId = ApplicationIdentifier(UUID(0, 0)),
            path = Path("$uploadsDirPath/file.txt"),
            type = DocumentType.Contract,
            key = ""
        )

        val ds = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)

        // WHEN
        val key = ds.save(metadata, content.byteInputStream())
        val updatedMetadata = metadata.copy(key = key)
        val anotherDS = FsDocumentContentDataSource(secret, Dispatchers.Unconfined)
        val decrypted = anotherDS.get(updatedMetadata)
            .readAllBytes()
            .let(::String)

        // THEN
        assertEquals(content, decrypted)
    }
}
