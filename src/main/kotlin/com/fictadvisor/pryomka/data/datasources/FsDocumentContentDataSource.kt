package com.fictadvisor.pryomka.data.datasources

import com.fictadvisor.pryomka.domain.datasource.DocumentContentDataSource
import com.fictadvisor.pryomka.domain.models.DocumentKey
import com.fictadvisor.pryomka.domain.models.DocumentMetadata
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Suppress("BlockingMethodInNonBlockingContext")
class FsDocumentContentDataSource(
    private val secret: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DocumentContentDataSource {
    private val secretKey by lazy {
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
