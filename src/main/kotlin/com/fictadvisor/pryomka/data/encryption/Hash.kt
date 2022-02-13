package com.fictadvisor.pryomka.data.encryption

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hash {
    private val rand = SecureRandom()
    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 512
    private const val SALT_LENGTH = 16
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"

    fun verify(data: String, hash: String, salt: String): Boolean {
        return hash(data, salt) == hash
    }

    fun hash(data: String, salt: String = generateSalt()): String {
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

    operator fun invoke(data: String, salt: String = generateSalt()) = hash(data, salt)

    fun generateSalt(length: Int = SALT_LENGTH): String {
        require(length >= 1)

        val salt = ByteArray(length)
        rand.nextBytes(salt)

        return Base64.getEncoder().encodeToString(salt)
    }
}
