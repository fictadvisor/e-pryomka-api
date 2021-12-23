package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.models.DocumentKey
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.use

@Suppress("BlockingMethodInNonBlockingContext")
class FsDocumentContentDataSource(
    private val secret: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DocumentContentDataSource {
    private val secretKey by lazy {
        println(secret)
        val decodedKey = Base64.getDecoder().decode(secret)
        SecretKeySpec(decodedKey, 0, decodedKey.size, KEY_GEN_ALGORITHM)
    }

    private val keyGen = KeyGenerator.getInstance(KEY_GEN_ALGORITHM).also {
        it.init(KEY_SIZE)
    }

    override suspend fun save(
        document: DocumentMetadata,
        data: InputStream,
    ): DocumentKey = withContext(dispatcher) {
        val documentKey = keyGen.generateKey()
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, documentKey)
        }

        val file = File(document.path.value)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val fileOutput = FileOutputStream(file).buffered(512)
        fileOutput.write(cipher.iv.size)
        fileOutput.write(cipher.iv)

        data.use { input ->
            CipherOutputStream(fileOutput, cipher).use { output ->
                input.transferTo(output)
            }
        }

        encryptKey(documentKey.encoded)
    }

    private fun encryptKey(key: ByteArray): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }

        return cipher.doFinal(key).let { encryptedKey ->
            val keyAndIv = byteArrayOf(cipher.iv.size.toByte()) + cipher.iv + encryptedKey
            Base64.getEncoder().encodeToString(keyAndIv)
        }
    }

    override suspend fun get(
        document: DocumentMetadata,
    ): InputStream = withContext(Dispatchers.IO) {
        val documentKey = decryptKey(document.key)
        val fileStream = FileInputStream(document.path.value).buffered(512)
        val ivSize = fileStream.read()
        val iv = IvParameterSpec(fileStream.readNBytes(ivSize))

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, documentKey, iv)
        }

        CipherInputStream(fileStream, cipher)
    }

    private fun decryptKey(key: DocumentKey): SecretKey {
        val base64Decoded = Base64.getDecoder().decode(key)
        val ivSize = base64Decoded.first()
        val iv = IvParameterSpec(
            base64Decoded.slice(1..ivSize).toByteArray()
        )
        val encryptedKey = base64Decoded.drop(ivSize + 1).toByteArray()

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, secretKey, iv)
        }

        val decryptedKey = cipher.doFinal(encryptedKey)
        return SecretKeySpec(decryptedKey, 0, decryptedKey.size, KEY_GEN_ALGORITHM)
    }

    override suspend fun delete(document: DocumentMetadata) {
        File(document.path.value).delete()
    }

    companion object {
        private const val KEY_GEN_ALGORITHM = "AES"
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
        const val KEY_SIZE = 256 // bits
    }
}

//fun main() {
//    val text = """
//        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
//        eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
//        ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
//        aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit
//        in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur
//        sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
//        mollit anim id est laborum
//    """.trimIndent()
//
//    val input = text.byteInputStream()
//
//    val ds = FsDocumentDataSource("whoy/NHOd2aOxzXjqkug1oSQrAu+0leYgKaUZaRsGdY=")
//
//    runBlocking {
//        val d1 = Document(Path("uploads/croc_encrypted"))
//        val d2 = Document(Path("uploads/lorem_encrypted.txt"))
//        val k1 = ds.saveDocument(d1, FileInputStream("uploads/croc.jpg"))
//        val k2 = ds.saveDocument(d2, input)
//
//        ds.getDocument(d1, k1).transferTo(FileOutputStream("uploads/croc_decrypted.jpg"))
//        ds.getDocument(d2, k2).transferTo(FileOutputStream("uploads/lorem_decrypted.txt"))
//    }
//}
