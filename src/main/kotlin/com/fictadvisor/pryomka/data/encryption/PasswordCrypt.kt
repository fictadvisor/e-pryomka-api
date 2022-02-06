package com.fictadvisor.pryomka.data.encryption

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordCrypt {
    private val rand = SecureRandom()
    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 512
    private const val SALT_LENGTH = 16
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"

    fun verify(password: String, hashedPassword: String, salt: String): Boolean {
        return hashPassword(password, salt) == hashedPassword
    }

    fun hashPassword(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt.toByteArray(),
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val hashedPassword = SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(spec)
                .encoded
            Base64.getEncoder().encodeToString(hashedPassword)
        } finally {
            spec.clearPassword()
        }
    }

    fun generateSalt(length: Int = SALT_LENGTH): String {
        require(length >= 1)

        val salt = ByteArray(length)
        rand.nextBytes(salt)

        return Base64.getEncoder().encodeToString(salt)
    }
}
