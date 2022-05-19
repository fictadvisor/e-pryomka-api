package com.fictadvisor.pryomka.domain.mappers

import com.fictadvisor.pryomka.api.mappers.toTelegramData
import com.fictadvisor.pryomka.telegramData
import com.fictadvisor.pryomka.telegramDataDto
import kotlin.test.Test
import kotlin.test.assertEquals

class MappersTest {
    @Test
    fun `TelegramDataDto to TelegramData`() {
        // GIVEN
        val dto = telegramDataDto(
            authDate = 123,
            id = 456,
            firstName = "lelouch",
            lastName = "lamperouge",
            userName = null,
            photoUrl = "http://photo.com",
            tgBotId = "1",
        )
        val data = telegramData(
            authDate = 123,
            id = 456,
            firstName = "lelouch",
            lastName = "lamperouge",
            userName = null,
            photoUrl = "http://photo.com",
            tgBotId = "1",
        )

        // WHEN+THEN
        assertEquals(data, dto.toTelegramData())
    }
}
