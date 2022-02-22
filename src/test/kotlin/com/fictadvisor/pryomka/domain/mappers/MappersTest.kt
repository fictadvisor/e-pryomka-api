package com.fictadvisor.pryomka.domain.mappers

import com.fictadvisor.pryomka.api.dto.TelegramDataDto
import com.fictadvisor.pryomka.api.mappers.toTelegramData
import com.fictadvisor.pryomka.domain.models.TelegramData
import kotlin.test.Test
import kotlin.test.assertEquals

class MappersTest {
    @Test
    fun `TelegramDataDto to TelegramData`() {
        // GIVEN
        val dto = TelegramDataDto(
            authDate = 123,
            id = 456,
            firstName = "lelouch",
            lastName = "lamperouge",
            userName = null,
            photoUrl = "http://photo.com",
            hash = "qwerty"
        )
        val data = TelegramData(
            authDate = 123,
            id = 456,
            firstName = "lelouch",
            lastName = "lamperouge",
            userName = null,
            photoUrl = "http://photo.com",
            hash = "qwerty"
        )

        // WHEN+THEN
        assertEquals(data, dto.toTelegramData())
    }
}
