package com.fictadvisor.pryomka.domain.mappers

import com.fictadvisor.pryomka.domain.models.TelegramData
import com.fictadvisor.pryomka.domain.models.User
import com.fictadvisor.pryomka.domain.models.generateUserId

fun TelegramData.toEntrant() = User.Entrant(
    id = generateUserId(),
    telegramId = id,
    firstName = firstName,
    lastName = lastName,
    userName = userName,
    photoUrl = photoUrl,
)
