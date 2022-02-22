package com.fictadvisor.pryomka.data.encryption

import com.fictadvisor.pryomka.telegramData
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class HashTest {
    private val tgBotId = "4002278938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp"

    @Test
    fun `generated salt must be random`() {
        // WHEN
        val salt1 = Hash.generateSalt(10)
        val salt2 = Hash.generateSalt(10)
        val salt3 = Hash.generateSalt(10)

        // THEN
        assertNotEquals(salt1, salt2)
        assertNotEquals(salt1, salt3)
        assertNotEquals(salt2, salt3)
    }

    @Test
    fun `should throw error if salt length is invalid`() {
        // WHEN+THEN
        assertThrows<IllegalArgumentException> { Hash.generateSalt(0) }
        assertThrows<IllegalArgumentException> { Hash.generateSalt(-1) }
    }

    @Test
    fun `should generate salt with default length`() {
        // WHEN
        val salt = Hash.generateSalt()

        // THEN
        assertTrue(salt.isNotBlank())
    }

    @Test
    fun `hashed message should not be same as plain message`() {
        // GIVEN
        val message = """
            The human heart is the source of all our power.
            We fight with the power of our hearts."
        """.trimIndent()

        val salt = Hash.generateSalt()

        // WHEN
        val hash = Hash(message, salt)

        // THEN
        assertNotEquals(message, hash)
    }

    @Test
    fun `hashing with same salt shall produce same output`() {
        // GIVEN
        val message = """
            The human heart is the source of all our power.
            We fight with the power of our hearts."
        """.trimIndent()

        val salt = Hash.generateSalt()

        // WHEN
        val hash1 = Hash(message, salt)
        val hash2 = Hash(message, salt)

        // THEN
        assertEquals(hash1, hash2)
    }

    @Test
    fun `hashing with different salt shall produce different output`() {
        // GIVEN
        val message = """
            The human heart is the source of all our power.
            We fight with the power of our hearts."
        """.trimIndent()

        val salt1 = Hash.generateSalt()
        val salt2 = Hash.generateSalt()

        // WHEN
        val hash1 = Hash(message, salt1)
        val hash2 = Hash(message, salt2)

        // THEN
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hash can be verified using original message and salt`() {
        // GIVEN
        val message = """
            The human heart is the source of all our power.
            We fight with the power of our hearts."
        """.trimIndent()

        val salt = Hash.generateSalt()

        // WHEN
        val hash = Hash(message, salt)

        // THEN
        assertTrue(Hash.verify(message, hash, salt))
    }

    @Test
    fun `must hash telegram data with given bot id`() {
        // GIVEN
        val data = telegramData()

        // WHEN
        val hash = Hash.hashTelegramData(data, tgBotId)

        // THEN
        assertTrue(hash.isNotBlank())
    }

    @Test
    fun `same bot id must produce same hash`() {
        // GIVEN
        val data = telegramData()

        // WHEN
        val hash1 = Hash.hashTelegramData(data, tgBotId)
        val hash2 = Hash.hashTelegramData(data, tgBotId)

        // THEN
        assertEquals(hash1, hash2)
    }

    @Test
    fun `data hash can be verified with bot id`() {
        // GIVEN
        val data = telegramData()
        val hash = Hash.hashTelegramData(data, tgBotId)
        val hashedData = data.copy(hash = hash)

        // WHEN+THEN
        assertTrue {
            Hash.verifyTelegramData(hashedData, tgBotId)
        }
    }

    @Test
    fun `should return false if data signed with another bot id`() {
        // GIVEN
        val data = telegramData()
        val hash = Hash.hashTelegramData(data, tgBotId)
        val hashedData = data.copy(hash = hash)
        val anotherBotId = "1234578938:ABGEHE_2_9razcj9t1zAw1JaYA31zz16bQp"

        // WHEN+THEN
        assertFalse {
            Hash.verifyTelegramData(hashedData, anotherBotId)
        }
    }
}
