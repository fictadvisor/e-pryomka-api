package com.fictadvisor.pryomka.data.encryption

import com.fictadvisor.pryomka.utils.toHexString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
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

    fun verifyTelegramData(data: Map<String, JsonElement>, tgBotId: String): Boolean {
        val hash = (data["hash"] as JsonPrimitive).content
        return hash == hashTelegramData(data - "hash", tgBotId)
    }

    fun hashTelegramData(data: Map<String, JsonElement>, tgBotId: String): String {
        val dataCheckString = data.keys
            .sorted()
            .joinToString("\n") { key ->
                val value = (data[key] as JsonPrimitive).content
                "$key=${value}"
            }

        val secretKey = SecretKeySpec(
            MessageDigest.getInstance("SHA-256").digest(tgBotId.toByteArray()),
            "HmacSHA256"
        )

        val hmac = Mac.getInstance("HmacSHA256")
        hmac.init(secretKey)
        return hmac.doFinal(dataCheckString.toByteArray()).toHexString()
    }
}
