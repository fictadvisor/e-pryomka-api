package com.fictadvisor.pryomka.data.encryption

import com.fictadvisor.pryomka.domain.models.TelegramData
import com.fictadvisor.pryomka.utils.toHexString
import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object Hash {
    private val rand = SecureRandom()
    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 512
    private const val SALT_LENGTH = 16
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"

    fun verify(data: String, hash: String, salt: String): Boolean {
        return hash(data, salt) == hash
    }

    fun hash(data: String, salt: String): String {
        val spec = PBEKeySpec(
            data.toCharArray(),
            salt.toByteArray(),
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val hash = SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(spec)
                .encoded
            Base64.getEncoder().encodeToString(hash)
        } finally {
            spec.clearPassword()
        }
    }

    operator fun invoke(data: String, salt: String) = hash(data, salt)

    fun generateSalt(length: Int = SALT_LENGTH): String {
        require(length >= 1)

        val salt = ByteArray(length)
        rand.nextBytes(salt)

        return Base64.getEncoder().encodeToString(salt)
    }

    fun verifyTelegramData(data: TelegramData, tgBotId: String): Boolean {
        return data.hash == hashTelegramData(data, tgBotId)
    }

    fun hashTelegramData(data: TelegramData, tgBotId: String): String {
        val dataCheckString = data.dataCheckString.toByteArray()

        val hmac = Mac.getInstance("HmacSHA256")

        val sha256 = MessageDigest.getInstance("SHA-256")
        val secretKey = SecretKeySpec(
            sha256.digest(tgBotId.toByteArray()),
            "HmacSHA256"
        )

        hmac.init(secretKey)
        return hmac.doFinal(dataCheckString).toHexString()
    }
}
